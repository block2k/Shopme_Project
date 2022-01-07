package com.shopme.product;

import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 18;
    public static final int SEARCH_RESULT_PER_PAGE = 18;

    @Autowired
    private ProductRepository productRepository;

    Page<Product> listByCategory(int pageNumber, Integer categoryId) {
        String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE);
        return productRepository.listByCategory(categoryId, categoryIdMatch, pageable);
    }

    Product getProduct(String alias) throws ProductNotFoundException {
        Product product = productRepository.findByAlias(alias);
        if (product == null) {
            throw new ProductNotFoundException("Product not found");
        }
        return product;
    }

    Page<Product> search(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, SEARCH_RESULT_PER_PAGE);
        return productRepository.search(keyword, pageable);
    }

}
