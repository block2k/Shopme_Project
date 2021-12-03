package com.shopme.admin.brands;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Brand;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {
    Brand findByName(String name);
    
    @Query("select b from Brand b where b.name like %:keyword%")
    Page<Brand> findAll(String keyword, Pageable pageable);
    
    @Query("select new Brand(b.id, b.name) from Brand b order by b.name asc")
    List<Brand> findAll();
}
