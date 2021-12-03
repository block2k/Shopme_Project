package com.shopme.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductDetailService {
    @Autowired
    private ProductDetailRepository productDetailRepository;

    public void deleteProductDetailsByProductId(Integer id){
        productDetailRepository.deleteProductDetailsByProductId(id);
    }
}
