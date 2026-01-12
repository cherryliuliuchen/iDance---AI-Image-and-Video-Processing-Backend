package com.ali.animateweb.controller;

import com.ali.animateweb.dto.CreateJobResponse;
import com.ali.animateweb.dto.GenerationStatusResponse;
import com.ali.animateweb.service.GenerationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/generations")
public class GenerationController {

    private final GenerationService generationService;

    public GenerationController(GenerationService generationService) {
        this.generationService = generationService;
    }

    // 1. multipart
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public CreateJobResponse createJobMultipart(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam("templateId") String templateId,
            @RequestParam("email") String email,
            @RequestParam(value = "ossUrl", required = false) String ossUrl
    ) {
        return generationService.createJob(file, templateId, email, ossUrl);
    }

    // 2. JSON
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public CreateJobResponse createJobJson(@RequestBody CreateJobJsonRequest req) {
        return generationService.createJob(
                null,
                req.getTemplateId(),
                req.getEmail(),
                req.getOssUrl()
        );
    }

    // 3. Step F: GET status
    @GetMapping(
            value = "/{generationId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerationStatusResponse getGeneration(@PathVariable String generationId) {
        return generationService.getGenerationStatus(generationId);
    }

    public static class CreateJobJsonRequest {
        private String templateId;
        private String email;
        private String ossUrl;

        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getOssUrl() { return ossUrl; }
        public void setOssUrl(String ossUrl) { this.ossUrl = ossUrl; }
    }
}
