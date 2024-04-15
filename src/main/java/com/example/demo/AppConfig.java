package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@ConfigurationProperties(prefix = "allowed-ip-addresses")
public class AppConfig {

    private String allowedIpAddresses;

    public List<String> getAllowedCryptos() {
        return allowedCryptos;
    }

    public void setAllowedCryptos(List<String> allowedCryptos) {
        this.allowedCryptos = allowedCryptos;
    }

    private List<String> allowedCryptos;

    // Getters and setters
    public String getAllowedIpAddresses() {
        return allowedIpAddresses;
    }

    public void setAllowedIpAddresses(String allowedIpAddresses) {
        this.allowedIpAddresses = allowedIpAddresses;
    }

}
