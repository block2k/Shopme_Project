package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repo;

    @Test
    public void testCreateRootCategory() {
	Category category = new Category("Electronics");
	Category savedCategory = repo.save(category);

	assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateSubCategory() {
	Category parent = new Category(7);
	Category subCate = new Category("iPhone", parent);
	repo.save(subCate);
    }

    @Test
    public void testGetCategory() {
	Category category = repo.findById(2).get();
	System.out.println(category.getName());

	Set<Category> childrent = category.getChildrent();
	for (Category subCate : childrent) {
	    System.out.println(subCate.getName());
	}
    }

    @Test
    public void testPrintCategoryStructure() {
	Iterable<Category> categories = repo.findAll();
	for (Category category : categories) {
	    if (category.getParent() == null) {
		System.out.println(category.getName());
		Set<Category> childrent = category.getChildrent();
		for (Category subCate : childrent) {
		    System.out.println("--" + subCate.getName());
		    printChildrent(subCate, 1);
		}
	    }
	}
    }

    private void printChildrent(Category parent, int subLevel) {
	int newSubLevel = subLevel + 1;
	for (Category subCategory : parent.getChildrent()) {
	    for (int i = 0; i < newSubLevel; i++) {
		System.out.print("--");
	    }
	    System.out.println(subCategory.getName());
	    printChildrent(subCategory, newSubLevel);
	}
    }

    @Test
    public void testListRootCategories() {
//	List<Category> listRootCategories = repo.findRootCategories();
//	listRootCategories.forEach(cat -> System.out.println(cat.getName()));
    }

    @Test
    public void testFindByName() {
	String name = "Computers1";
	Category findByName = repo.findByName(name);

	System.out.println(findByName.getName());
    }

    @Test
    public void testFindByAlias() {
	String name = "book1s";
	Category findByName = repo.findByAlias(name);

	System.out.println(findByName.getName());
    }

    @Test
    public void testEnableStatus() {
	repo.updateEnableStatus(2, false);
    }

    @Test
    public void testCountById() {
	System.out.println(repo.countById(1));
    }

}
