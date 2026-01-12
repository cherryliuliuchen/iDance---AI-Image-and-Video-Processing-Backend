package com.ali.animateweb.dto;

public class GenerationStatusResponse {
    private String generationId;
    private String status;
    private String taskId;
    private String videoUrl;
    private String message;

    public GenerationStatusResponse() {}

    public GenerationStatusResponse(String generationId, String status, String taskId, String videoUrl, String message) {
        this.generationId = generationId;
        this.status = status;
        this.taskId = taskId;
        this.videoUrl = videoUrl;
        this.message = message;
    }

    public static GenerationStatusResponse failed(String msg) {
        return new GenerationStatusResponse(null, "FAILED", null, null, msg);
    }

    public String getGenerationId() { return generationId; }
    public void setGenerationId(String generationId) { this.generationId = generationId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
