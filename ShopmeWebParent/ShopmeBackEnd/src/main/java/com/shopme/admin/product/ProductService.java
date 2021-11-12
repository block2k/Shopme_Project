package com.shopme.admin.product;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repo;

    public List<Product> listAll() {
	return (List<Product>) repo.findAll();
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
