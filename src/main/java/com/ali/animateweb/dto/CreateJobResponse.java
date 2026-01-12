package com.ali.animateweb.dto;

public class CreateJobResponse {

    private String generationId;
    private String status;     // CREATED / DETECTED / PENDING / FAILED ...
    private Boolean checkPass; // detect result
    private String taskId;     // Step E write
    private String message;    // Error information

    public CreateJobResponse() {}

    public CreateJobResponse(String generationId, String status, Boolean checkPass, String taskId, String message) {
        this.generationId = generationId;
        this.status = status;
        this.checkPass = checkPass;
        this.taskId = taskId;
        this.message = message;
    }

    public static CreateJobResponse failed(String message) {
        return new CreateJobResponse(null, "FAILED", null, null, message);
    }

    public String getGenerationId() {
        return generationId;
    }

    public void setGenerationId(String generationId) {
        this.generationId = generationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getCheckPass() {
        return checkPass;
    }

    public void setCheckPass(Boolean checkPass) {
        this.checkPass = checkPass;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
