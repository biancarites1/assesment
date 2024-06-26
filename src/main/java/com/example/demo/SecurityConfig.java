package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
/*security configuration class for the app to restrict users to access endpoints if they don't follow a set of rules*/
public class SecurityConfig {

    @Autowired
    private AppConfig appConfig;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/endpoint").hasIpAddress(appConfig.getAllowedIpAddresses()) // Allow only from localhost ip addresses
//                                .anyRequest().authenticated()
                );
        return http.build();
    }
}
