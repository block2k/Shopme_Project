package com.shopme.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void getAllEnabled(){
        List<Category> categories = categoryRepository.findAllEnabled();
        System.out.println(categories);
    }
    @Test
    public void findCategoryByAlias(){
        Category laptop_computers = categoryRepository.findByAliasEnabled("laptop_computers");
        System.out.println(laptop_computers);
    }


}
