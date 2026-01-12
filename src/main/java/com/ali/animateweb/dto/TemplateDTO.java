package com.ali.animateweb.dto;

public class TemplateDTO {
    private String templateId;
    private String name;
    private String previewUrl;

    public TemplateDTO() {}

    public TemplateDTO(String templateId, String name, String previewUrl) {
        this.templateId = templateId;
        this.name = name;
        this.previewUrl = previewUrl;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
}
