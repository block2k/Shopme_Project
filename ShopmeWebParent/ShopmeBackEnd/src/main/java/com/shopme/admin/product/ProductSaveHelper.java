package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ProductSaveHelper {
    static void deleteExtraImagesWeredRemovedOnForm(Product product) {
        String extraImageDir = "../product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);
        try {
            Files.list(dirPath).forEach(file -> {
                String fileName = file.toFile().getName();
                if (!product.containsImageName(fileName)) {
                    try {
                        Files.delete(file);
                        System.out.println("Delete thanh cong");
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static void setExistingExtraImageNames(String[] imageIds, String[] imageNames, Product product) {
        if (imageIds == null || imageIds.length == 0) return;
        Set<ProductImage> images = new HashSet<>();
        for (int i = 0; i < imageIds.length; i++) {
            Integer id = Integer.parseInt(imageIds[i]);
            String name = imageNames[i];
            images.add(new ProductImage(id, name, product));
        }
        product.setImages(images);
    }

    static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        // TODO Auto-generated method stub
        if (detailNames == null || detailNames.length == 0) {
            return;
        }
        for (int i = 0; i < detailNames.length; i++) {
            String name = detailNames[i];
            String value = detailValues[i];
            Integer id = Integer.parseInt(detailIDs[i]);
//            if (id != 0) {
//                product.addDetail(id, name, value);
//            }
            if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }
    }

    static void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultipart,
                                   Product product) throws IOException {
        // TODO Auto-generated method stub
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            String uploadDir = "../product-images/" + product.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        if (extraImageMultipart.length > 0) {
            String uploadDir = "../product-images/" + product.getId() + "/extras";
            for (MultipartFile multipartFile : extraImageMultipart) {
                if (multipartFile.isEmpty()) {
                    continue;
                }
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }
    }

    static void setNewExtraImageNames(MultipartFile[] extraImageMultipart, Product product) {
        // TODO Auto-generated method stub
        if (extraImageMultipart.length > 0) {
            for (MultipartFile multipartFile : extraImageMultipart) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    static void setMainImageName(MultipartFile mainImageMultipart, Product product) {
        // TODO Auto-generated method stub
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }
}
