package com.ali.animateweb.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TestDbService {

    private final JdbcTemplate jdbcTemplate;

    public TestDbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> testInsertAndQuery() {

        // Insert a data
        jdbcTemplate.update(
                "INSERT INTO test_jdbc (message) VALUES (?)",
                "Hello from Spring Boot JDBC"
        );

        // Query the data
        return jdbcTemplate.queryForList(
                "SELECT id, message, created_at FROM test_jdbc ORDER BY id DESC"
        );
    }
}
