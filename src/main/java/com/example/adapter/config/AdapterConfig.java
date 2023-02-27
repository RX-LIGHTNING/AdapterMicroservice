package com.example.adapter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// TODO: 10.02.2023 Make config an immutable
@Configuration
@ConfigurationProperties("link")
@Data
public class AdapterConfig {
    private String baseUrl;
    private String fineRequest;
    private String fineResult;
    private String fineAcknowledge;
    private int timeout;
    private int retryCount;
}
