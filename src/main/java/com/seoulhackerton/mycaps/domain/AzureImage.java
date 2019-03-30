package com.seoulhackerton.mycaps.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:azureimage.properties")
public class AzureImage {

    @Value("${eslow.subscriptionkey}")
    private String subscriptionKey;
    public String getSubscriptionKey() {
        return subscriptionKey;
    }
}