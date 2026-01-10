package com.rehund.healthcare.config.videosdkconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "videosdk")
public class VideoSDKProperties {
    private String apiKey;
    private String baseUrl;
}
