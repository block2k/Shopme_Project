package com.shopme;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        String categoryImagesDirName = "../categories-images";
//        Path categoryImagesDir = Paths.get(categoryImagesDirName);
//        String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();
//        registry.addResourceHandler("/categories-images/**").addResourceLocations("file:/" + categoryImagesPath + "/");

//        exposeDir("../brands-images", registry);
        exposeDir("../categories-images", registry);
        exposeDir("../product-images", registry);
    }

    private void exposeDir(String path, ResourceHandlerRegistry registry) {
        Path productImagesDir = Paths.get(path);
        String absolutePath = productImagesDir.toFile().getAbsolutePath();
        String logicalPath = "/" + path.replace("../", "") + "/**";
        registry.addResourceHandler(logicalPath).addResourceLocations("file:/" + absolutePath + "/");
    }
}
