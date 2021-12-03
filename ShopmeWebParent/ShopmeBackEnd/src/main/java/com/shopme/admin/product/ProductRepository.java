package com.shopme.admin.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Product;


public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

    Product findByName(String name);

    @Query("update Product p set p.enabled = :status where p.id = :id")
    @Modifying
    void updateStatus(Integer id, boolean status);

    @Query("select p from Product p where p.name like %:keyword% " +
            "or p.shortDescription like %:keyword% " +
            "or p.fullDescription like %:keyword% " +
            "or p.brand.name like %:keyword% " +
            "or p.category.name like %:keyword%")
    public Page<Product> findAll(String keyword, Pageable pageable);

    @Query("select p from Product p where p.category.id = :categoryId " +
            "or p.category.allParentIds like %:categoryIdMatch%")
    Page<Product> findAllInCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

    @Query("select p from Product p where (p.category.id = :categoryId " +
            "or p.category.allParentIds like %:categoryIdMatch%) and " +
            "(p.name like %:keyword% " +
            "or p.shortDescription like %:keyword% " +
            "or p.fullDescription like %:keyword% " +
            "or p.brand.name like %:keyword% " +
            "or p.category.name like %:keyword%)")
    Page<Product> searchInCategory(Integer categoryId, String categoryIdMatch, String keyword, Pageable pageable);

}
