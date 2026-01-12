package com.ali.animateweb.service;

import com.ali.animateweb.client.DashScopeClient;
import com.ali.animateweb.dto.UploadAssetResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class UploadService {

    private final JdbcTemplate jdbcTemplate;
    private final DashScopeClient dashScopeClient;

    public UploadService(JdbcTemplate jdbcTemplate, DashScopeClient dashScopeClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.dashScopeClient = dashScopeClient;
    }

    public UploadAssetResponse uploadImageToDashScopeOss(MultipartFile file, String email) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("file is required");
            }


            String contentType = file.getContentType();
            if (contentType != null &&
                    !contentType.equalsIgnoreCase("image/jpeg") &&
                    !contentType.equalsIgnoreCase("image/jpg") &&
                    !contentType.equalsIgnoreCase("image/png")) {
                throw new IllegalArgumentException("only jpeg/png is allowed");
            }

            UUID userId = upsertUserByEmail(email);

            String filename = safeFileName(file.getOriginalFilename());
            byte[] bytes = file.getBytes();

            String ossUrl = dashScopeClient.uploadToDashScopeOss(bytes, filename, "qwen-vl-plus");

            // upload_asset
            UUID assetId = UUID.randomUUID();
            jdbcTemplate.update("""
                INSERT INTO upload_asset(asset_id, user_id, provider, oss_url, file_name, content_type, file_size_bytes)
                VALUES (?, ?, 'dashscope', ?, ?, ?, ?)
            """, assetId, userId, ossUrl, filename, contentType, file.getSize());

            return new UploadAssetResponse(assetId.toString(), ossUrl, filename);

        } catch (Exception e) {
            throw new RuntimeException("uploadImageToDashScopeOss failed: " + e.getMessage(), e);
        }
    }

    private UUID upsertUserByEmail(String email) {
        if (email == null || email.isBlank()) return null;

        UUID existing = jdbcTemplate.query("""
            SELECT user_id FROM app_user WHERE email = ?
        """, rs -> rs.next() ? (UUID) rs.getObject("user_id") : null, email);

        if (existing != null) return existing;

        UUID newId = UUID.randomUUID();
        jdbcTemplate.update("""
            INSERT INTO app_user(user_id, email, created_at)
            VALUES (?, ?, now())
        """, newId, email);
        return newId;
    }

    private String safeFileName(String original) {
        if (original == null || original.isBlank()) {
            return "upload_" + System.currentTimeMillis() + ".jpg";
        }
        return original.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
