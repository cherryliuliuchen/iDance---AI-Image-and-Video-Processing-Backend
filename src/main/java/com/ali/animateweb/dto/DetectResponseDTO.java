package com.ali.animateweb.dto;

public class DetectResponseDTO {
    private boolean checkPass;
    private String reason;
    private String requestId;

    public DetectResponseDTO() {}

    public DetectResponseDTO(boolean checkPass, String reason, String requestId) {
        this.checkPass = checkPass;
        this.reason = reason;
        this.requestId = requestId;
    }

    public boolean isCheckPass() {
        return checkPass;
    }

    public void setCheckPass(boolean checkPass) {
        this.checkPass = checkPass;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
