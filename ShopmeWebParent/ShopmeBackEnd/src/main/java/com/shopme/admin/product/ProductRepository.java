package com.shopme.admin.product;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

    public Product findByName(String name);

    @Query("update Product p set p.enabled = :status where p.id = :id")
    @Modifying
    public void updateStatus(Integer id, boolean status);
}
