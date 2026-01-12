package com.ali.animateweb.controller;

import com.ali.animateweb.dto.UploadAssetResponse;
import com.ali.animateweb.service.UploadService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadAssetResponse uploadImage(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "email", required = false) String email
    ) {
        return uploadService.uploadImageToDashScopeOss(file, email);
    }
}
