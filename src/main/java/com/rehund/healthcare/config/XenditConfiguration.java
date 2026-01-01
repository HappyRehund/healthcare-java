package com.rehund.healthcare.config;

import com.xendit.Xendit;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class XenditConfiguration {

    @Value("${xendit.api-key}")
    private String xenditApiKey;

    @Bean
    public Xendit xenditClient(){
        Xendit.apiKey = xenditApiKey;
        return new Xendit();
    }

}
