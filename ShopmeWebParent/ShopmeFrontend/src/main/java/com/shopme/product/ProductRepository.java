package com.shopme.product;

import com.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product,Integer> {
    @Query("SELECT p from Product p where p.enabled = true and (p.category.id=:categoryId or p.category.allParentIds like %:categoryIdMatch%) order by p.name asc ")
    Page<Product> listByCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);
}
