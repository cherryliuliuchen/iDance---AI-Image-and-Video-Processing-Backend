package com.ali.animateweb.dto;

public class JobStatusResponse {
    private String generationId;
    private String status;
    private String taskId;
    private String videoUrl;

    public String getGenerationId() { return generationId; }
    public void setGenerationId(String generationId) { this.generationId = generationId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}
