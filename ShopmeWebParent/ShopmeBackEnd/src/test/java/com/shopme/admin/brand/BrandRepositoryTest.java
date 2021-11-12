package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.brands.BrandRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {
    @Autowired
    BrandRepository repository;

    @Test
    public void testCreateBrand() {
	Brand brand = new Brand();
	brand.setLogo("image-thumbnail.png");
	brand.setName("Apple1");
	brand.getCategories().add(new Category(7));

	Brand save = repository.save(brand);

	assertThat(save).isNotNull();
    }

    @Test
    public void testGetAllBrand() {
	List<Brand> findAll = repository.findAll();

	for (Brand brand : findAll) {
	    System.out.println(brand);
	}
    }

    @Test
    public void testDeleteBrandById() {
	repository.deleteById(4);
    }

    @Test
    public void testUpdateBrand() {
	Brand brand = repository.findById(3).get();

	Set<Category> categories = brand.getCategories();
	System.out.println(categories);
    }

    @Test
    public void testFindByName() {
	Brand findByName = repository.findByName("Acer1");
	System.out.println(findByName.getName());
    }

    @Test
    public void testSearchByNameAndPaging() {
	Sort sort = Sort.by("name");
	Pageable pageable = PageRequest.of(1 - 1, 4, sort.ascending());
	Page<Brand> findAll = repository.findAll("a", pageable);
	List<Brand> content = findAll.getContent();
	for (Brand brand : content) {
	    System.out.println(brand.getName());
	}
    }

}
