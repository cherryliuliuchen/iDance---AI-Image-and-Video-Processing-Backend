package com.ali.animateweb.service;

import com.ali.animateweb.dto.CreateJobResponse;
import com.ali.animateweb.dto.GenerationStatusResponse;
import org.springframework.web.multipart.MultipartFile;

public interface GenerationService {
    CreateJobResponse createJob(MultipartFile file, String templateId, String email, String ossUrl);

    // Step F
    GenerationStatusResponse getGenerationStatus(String generationId);
}
