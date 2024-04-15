package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "myapp")
public class AppConfig {

    private String allowedIpAddresses;

    public String getAllowedCryptos() {
        return allowedCryptos;
    }

    public void setAllowedCryptos(String allowedCryptos) {
        this.allowedCryptos = allowedCryptos;
    }

    private String allowedCryptos;

    // Getters and setters
    public String getAllowedIpAddresses() {
        return allowedIpAddresses;
    }

    public void setAllowedIpAddresses(String allowedIpAddresses) {
        this.allowedIpAddresses = allowedIpAddresses;
    }

}
