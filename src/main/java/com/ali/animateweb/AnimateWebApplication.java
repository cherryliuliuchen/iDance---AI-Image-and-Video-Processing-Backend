package com.ali.animateweb;

import com.ali.animateweb.config.DashScopeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DashScopeProperties.class)
public class AnimateWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnimateWebApplication.class, args);
    }
}
