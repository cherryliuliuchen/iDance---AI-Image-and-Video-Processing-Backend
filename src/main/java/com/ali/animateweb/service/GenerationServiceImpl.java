package com.ali.animateweb.service;

import com.ali.animateweb.client.DashScopeClient;
import com.ali.animateweb.dto.CreateJobResponse;
import com.ali.animateweb.dto.DetectResponseDTO;
import com.ali.animateweb.dto.GenerationStatusResponse;
import com.ali.animateweb.dto.SubmitTaskResponseDTO;
import com.ali.animateweb.dto.TaskStatusResponseDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class GenerationServiceImpl implements GenerationService {

    private final JdbcTemplate jdbcTemplate;
    private final DashScopeClient dashScopeClient;

    public GenerationServiceImpl(JdbcTemplate jdbcTemplate, DashScopeClient dashScopeClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.dashScopeClient = dashScopeClient;
    }

    @Override
    public CreateJobResponse createJob(MultipartFile file, String templateId, String email, String ossUrl) {
        try {
            boolean hasFile = (file != null && !file.isEmpty());
            boolean hasOssUrl = (ossUrl != null && !ossUrl.isBlank());
            if (!hasFile && !hasOssUrl) {
                return CreateJobResponse.failed("Please upload the picture. ");
            }

            UUID userId = upsertUserByEmail(email);

            String finalOssUrl;
            if (hasOssUrl) {
                finalOssUrl = ossUrl.trim();
            } else {
                String filename = safeFileName(file.getOriginalFilename());
                byte[] bytes = file.getBytes();
                finalOssUrl = dashScopeClient.uploadToDashScopeOss(bytes, filename, "qwen-vl-plus");
            }

            UUID assetId = UUID.randomUUID();
            jdbcTemplate.update("""
                INSERT INTO upload_asset(asset_id, user_id, provider, oss_url, file_name, content_type, file_size_bytes)
                VALUES (?, ?, 'dashscope', ?, ?, ?, ?)
            """,
                    assetId,
                    userId,
                    finalOssUrl,
                    hasFile ? safeFileName(file.getOriginalFilename()) : null,
                    hasFile ? file.getContentType() : null,
                    hasFile ? file.getSize() : null
            );

            DetectResponseDTO detect = dashScopeClient.detect(finalOssUrl);

            UUID detectionId = UUID.randomUUID();
            jdbcTemplate.update("""
                INSERT INTO image_detection(detection_id, asset_id, model, check_pass, reason, request_id)
                VALUES (?, ?, 'animate-anyone-detect-gen2', ?, ?, ?)
            """,
                    detectionId,
                    assetId,
                    detect.isCheckPass(),
                    detect.getReason(),
                    detect.getRequestId()
            );

            if (!detect.isCheckPass()) {
                return new CreateJobResponse(null, "FAILED", false, null, "Please re-upload the image if it is not up to standard.");
            }

            UUID generationId = UUID.randomUUID();
            jdbcTemplate.update("""
                INSERT INTO video_generation(generation_id, user_id, detection_id, template_id, model, status, created_at, updated_at)
                VALUES (?, ?, ?, ?, 'animate-anyone-gen2', 'DETECTED', now(), now())
            """, generationId, userId, detectionId, templateId);

            SubmitTaskResponseDTO submit = dashScopeClient.submitVideoSynthesis(finalOssUrl, templateId);

            String newStatus = (submit.getTaskStatus() == null || submit.getTaskStatus().isBlank())
                    ? "PENDING"
                    : submit.getTaskStatus();

            jdbcTemplate.update("""
                UPDATE video_generation
                SET task_id = ?, status = ?, request_id = ?, updated_at = now()
                WHERE generation_id = ?
            """, submit.getTaskId(), newStatus, submit.getRequestId(), generationId);

            return new CreateJobResponse(
                    generationId.toString(),
                    newStatus,
                    true,
                    submit.getTaskId(),
                    null
            );

        } catch (Exception e) {
            return CreateJobResponse.failed("createJob failed: " + e.getMessage());
        }
    }

    // =========================
    // Step F: GET /generations/{id}
    // =========================
    @Override
    public GenerationStatusResponse getGenerationStatus(String generationIdStr) {
        try {
            UUID generationId = UUID.fromString(generationIdStr);

            // 1) Read task_id / status / video_url from DB
            GenerationStatusRow row = jdbcTemplate.query("""
                SELECT generation_id, status, task_id, video_url
                FROM video_generation
                WHERE generation_id = ?
            """, rs -> {
                if (!rs.next()) return null;
                GenerationStatusRow r = new GenerationStatusRow();
                r.generationId = rs.getObject("generation_id", UUID.class);
                r.status = rs.getString("status");
                r.taskId = rs.getString("task_id");
                r.videoUrl = rs.getString("video_url");
                return r;
            }, generationId);

            if (row == null) {
                return GenerationStatusResponse.failed("generation not found");
            }

            // 2) If do not have the  task_id
            if (row.taskId == null || row.taskId.isBlank()) {
                return new GenerationStatusResponse(
                        row.generationId.toString(),
                        row.status,
                        null,
                        row.videoUrl,
                        "task_id is empty"
                );
            }

            // 3)  DashScope tasks/{taskId}
            TaskStatusResponseDTO task = dashScopeClient.getTaskStatus(row.taskId);

            String latestStatus = task.getTaskStatus();
            String latestVideoUrl = task.getVideoUrl();
            String latestRequestId = task.getRequestId();

            if (latestStatus == null || latestStatus.isBlank()) {
                // DashScope returned the database value before specifying the status.
                return new GenerationStatusResponse(
                        row.generationId.toString(),
                        row.status,
                        row.taskId,
                        row.videoUrl,
                        "task_status is empty"
                );
            }

            // 4) Update DB (write back when state changes)
            // - SUCCEEDED: Update status + video_url
            // - RUNNING/PENDING: Update status
            // - FAILED: Update status
            jdbcTemplate.update("""
                UPDATE video_generation
                SET status = ?, video_url = COALESCE(?, video_url), request_id = COALESCE(?, request_id), updated_at = now()
                WHERE generation_id = ?
            """, latestStatus, latestVideoUrl, latestRequestId, generationId);

            // 5) Return to front end
            return new GenerationStatusResponse(
                    row.generationId.toString(),
                    latestStatus,
                    row.taskId,
                    latestVideoUrl != null ? latestVideoUrl : row.videoUrl,
                    null
            );

        } catch (IllegalArgumentException badUuid) {
            return GenerationStatusResponse.failed("invalid generationId");
        } catch (Exception e) {
            return GenerationStatusResponse.failed("getGenerationStatus failed: " + e.getMessage());
        }
    }

    // ===== helpers =====

    private static class GenerationStatusRow {
        UUID generationId;
        String status;
        String taskId;
        String videoUrl;
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
