package com.evms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC configuration for serving static resources.
 * <p>
 * Maps the {@code /uploads/**} URL path to the local filesystem directory
 * where signature images are stored. This replaces Express's
 * {@code express.static} middleware from the Node.js backend.
 * <p>
 * Example: A request to {@code GET /uploads/signatures/sig_123.png} will
 * serve the file from {@code ./uploads/signatures/sig_123.png}.
 *
 * @author EVMS Team
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** Path to the upload directory from application.properties. */
    @Value("${file.upload-dir:./uploads/signatures}")
    private String uploadDir;

    /**
     * Registers the uploads directory as a static resource handler.
     * <p>
     * The {@code crossOriginResourcePolicy} is handled by the CORS configuration
     * rather than here, matching the original Node.js Helmet configuration.
     *
     * @param registry the resource handler registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize().getParent();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/");
    }
}
