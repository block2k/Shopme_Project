package com.shopme.admin.product;

import com.shopme.common.entity.ProductDetail;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProductDetailRepository extends CrudRepository<ProductDetail, Integer> {

    @Modifying
    @Query("delete from ProductDetail pi where pi.product.id = :id")
    void deleteProductDetailsByProductId(Integer id);
}
