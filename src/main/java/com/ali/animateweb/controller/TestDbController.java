package com.ali.animateweb.controller;

import com.ali.animateweb.service.TestDbService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TestDbController {

    private final TestDbService testDbService;

    public TestDbController(TestDbService testDbService) {
        this.testDbService = testDbService;
    }

    @GetMapping("/test/db")
    public List<Map<String, Object>> testDb() {
        return testDbService.testInsertAndQuery();
    }
}
