package com.ali.animateweb.controller;

import com.ali.animateweb.constant.ApiConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.GENERATION_PREFIX)
public class TemplateController {

    @GetMapping(ApiConstants.TEMPLATES)
    public List<TemplateItem> listTemplates() {
        return List.of(
                new TemplateItem("AACT.8090e67b.ADo5Qtp4EfCu1wAWPj1Rgg.anXxTRJZ", "Dance 01", null),
                new TemplateItem(
                        "AACT.8090e67b.zNZS1u2FEfCnyAAWPj1RRQ.Viw40txe",
                        "Dance 02",
                        null
                )
        );
    }

    public record TemplateItem(String templateId, String name, String previewUrl) {}
}
