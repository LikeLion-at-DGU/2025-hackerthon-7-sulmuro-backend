// src/main/java/.../config/StaticResourceConfig.java
package com.example.sulmuro_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.*;

import java.util.concurrent.TimeUnit;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.upload.root:${user.home}/uploads}")
    private String uploadRoot;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String loc = "file:" + (uploadRoot.endsWith("/") ? uploadRoot : uploadRoot + "/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(loc)
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic());
    }
}
