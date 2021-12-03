package com.shopme.admin.product;

import java.util.Date;
import java.util.List;

import com.shopme.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repo;

    public static final int PRODUCS_PER_PAGE = 5;

    public List<Product> listAll() {
        return (List<Product>) repo.findAll();
    }

    public Page<Product> listByPage(int pageNum, String keyword, String sortField, String sortDir, Integer categoryId) {
        Sort sort = Sort.by(sortField);
        if (sortDir == null) {
            sortDir = "asc";
        }
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCS_PER_PAGE, sort);
        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
                return repo.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            }
            return repo.findAll(keyword.trim(), pageable);
        }
        if (categoryId != null && categoryId > 0) {
            String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
            return repo.findAllInCategory(categoryId, categoryIdMatch, pageable);
        }
        return repo.findAll(pageable);
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }
        if (product.getAlias().isEmpty() || product.getAlias() == null) {
            String defaultAlias = product.getName().replaceAll("\\s+", "-");
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getAlias().replaceAll("\\s+", "-"));
        }
        product.setUpdatedTime(new Date());

        return repo.save(product);
    }

    public String checkUnique(Integer id, String name) {
        // nếu đang create new product
        Product findByName = repo.findByName(name);

        if (id == null) {
            if (findByName != null) {
                return "Duplicate";
            }
            // trường hợp đang edit product
        } else {
            if (findByName != null && findByName.getId() != id) {
                return "Duplicate";
            }
        }
        return "OK";
    }

    public void updateStatus(Integer id, boolean status) {
        repo.updateStatus(id, status);
    }

    public void deleteProduct(Integer id) {
        repo.deleteById(id);
    }

    public Product get(Integer id) throws Exception {
        try {
            return repo.findById(id).get();
        } catch (Exception e) {
            throw new Exception("Không tìm thấy Product ID (" + id + ")");
        }
    }
}
