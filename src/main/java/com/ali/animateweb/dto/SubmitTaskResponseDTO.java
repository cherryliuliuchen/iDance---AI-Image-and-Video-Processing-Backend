package com.ali.animateweb.dto;

public class SubmitTaskResponseDTO {

    private String taskId;
    private String taskStatus;
    private String requestId;

    public SubmitTaskResponseDTO() {}

    public SubmitTaskResponseDTO(String taskId, String taskStatus, String requestId) {
        this.taskId = taskId;
        this.taskStatus = taskStatus;
        this.requestId = requestId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
