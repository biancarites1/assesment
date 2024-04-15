package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "crypto")
/*read data from configuration file*/
public class AppConfig {

    private String allowedIpAddresses;

    public String getAllowedCryptos() {
        return allowedCryptos;
    }

    public void setAllowedCryptos(String allowedCryptos) {
        this.allowedCryptos = allowedCryptos;
    }

    private String allowedCryptos;

    public String getCryptoDirectory() {
        return cryptoDirectory;
    }

    public void setCryptoDirectory(String cryptoDirectory) {
        this.cryptoDirectory = cryptoDirectory;
    }

    private String cryptoDirectory;

    // Getters and setters
    public String getAllowedIpAddresses() {
        return allowedIpAddresses;
    }

    public void setAllowedIpAddresses(String allowedIpAddresses) {
        this.allowedIpAddresses = allowedIpAddresses;
    }

}
