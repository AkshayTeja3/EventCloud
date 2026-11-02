package com.eventcloud.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
@Configuration
public class UploadResourceConfig implements WebMvcConfigurer {
 @Value("${app.upload-dir}") String dir;
 @Override public void addResourceHandlers(ResourceHandlerRegistry registry){String path=Paths.get(dir).toAbsolutePath().normalize().toUri().toString();registry.addResourceHandler("/uploads/**").addResourceLocations(path);}
}
