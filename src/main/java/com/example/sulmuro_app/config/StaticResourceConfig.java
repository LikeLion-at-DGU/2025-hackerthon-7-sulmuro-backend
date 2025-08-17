package com.example.sulmuro_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.upload.root:${user.home}/uploads}")
    private String uploadRoot;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String loc = "file:" + (uploadRoot.endsWith("/") ? uploadRoot : uploadRoot + "/");
        registry.addResourceHandler("/uploads/**").addResourceLocations(loc);
    }
}
