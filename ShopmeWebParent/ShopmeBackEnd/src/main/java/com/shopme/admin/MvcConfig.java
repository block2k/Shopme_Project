package com.shopme.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	String dirName = "user-photo";
	Path userPhotosDir = Paths.get(dirName);
	String userPhotoPath = userPhotosDir.toFile().getAbsolutePath();
	registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + userPhotoPath + "/");

	String categoryImagesDirName = "../categories-images";
	Path categoryImagesDir = Paths.get(categoryImagesDirName);
	String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();
	registry.addResourceHandler("/categories-images/**").addResourceLocations("file:/" + categoryImagesPath + "/");

	String brandImagesDirName = "../brands-images";
	Path brandImagesDir = Paths.get(brandImagesDirName);
	String brandImagesPath = brandImagesDir.toFile().getAbsolutePath();
	registry.addResourceHandler("/brands-images/**").addResourceLocations("file:/" + brandImagesPath + "/");

	String productImagesDirName = "../product-images";
	Path productImagesDir = Paths.get(productImagesDirName);
	String productImagesPath = productImagesDir.toFile().getAbsolutePath();
	registry.addResourceHandler("/product-images/**").addResourceLocations("file:/" + productImagesPath + "/");
    }

}
