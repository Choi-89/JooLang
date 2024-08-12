package com.project.FreeCycle.Config;

//src\main\java\com\myapp\WebConfig.java

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    String ipAddress;
    String frontEndPort;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/test/ip")
                .allowCredentials(true)
                .allowedOrigins("http://"+this.ipAddress+":"+this.frontEndPort);
    }
}
