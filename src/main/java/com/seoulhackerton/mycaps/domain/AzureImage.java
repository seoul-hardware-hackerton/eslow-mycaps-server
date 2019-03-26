package com.seoulhackerton.mycaps.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:azureimage.properties")
public class AzureImage {

    @Value("${eslow.subscriptionkey}")
    private String subscriptionKey;

    @Value("${eslow.urlbase}")
    private String urlBase;

    public String getSubscriptionKey() {
        return subscriptionKey;
    }

    public void setSubscriptionKey(String subscriptionKey) {
        this.subscriptionKey = subscriptionKey;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }
}
