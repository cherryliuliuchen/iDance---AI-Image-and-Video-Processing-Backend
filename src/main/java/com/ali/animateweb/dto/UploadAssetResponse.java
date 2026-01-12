package com.ali.animateweb.dto;

public class UploadAssetResponse {
    private String assetId;
    private String ossUrl;
    private String fileName;

    public UploadAssetResponse() {}

    public UploadAssetResponse(String assetId, String ossUrl, String fileName) {
        this.assetId = assetId;
        this.ossUrl = ossUrl;
        this.fileName = fileName;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getOssUrl() {
        return ossUrl;
    }

    public void setOssUrl(String ossUrl) {
        this.ossUrl = ossUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
