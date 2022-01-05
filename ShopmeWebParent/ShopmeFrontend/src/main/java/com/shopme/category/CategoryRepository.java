package com.shopme.category;

import com.shopme.common.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
    @Query("select c from Category c where c.enabled = true order by c.name asc ")
    public List<Category> findAllEnabled();

    @Query("select c from Category c where c.enabled = true and c.alias = :alias")
    public Category findByAliasEnabled(String alias);
}
