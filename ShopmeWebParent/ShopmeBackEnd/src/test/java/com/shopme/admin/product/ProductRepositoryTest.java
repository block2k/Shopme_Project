package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductDetailRepository productImageRepository;

    @Test
    public void testDeleteDetail() {
        Integer id = 40;
        productImageRepository.deleteProductDetailsByProductId(id);
    }

    @Test
    public void testCreateProduct() {
        Brand brand = entityManager.find(Brand.class, 1);
        Category category = entityManager.find(Category.class, 6);
        for (int i = 0; i < 100; i++) {
            Product product = new Product();
            product.setName("Laptop MSI Modern " + i);
            product.setMainImage("1.jpg");
            product.setShortDescription("Lorem Ipsum " + i);
            product.setFullDescription("It is a long established ");
            product.setAlias("msi_modern_141" + i);
            product.setBrand(brand);
            product.setCategory(category);
            product.setPrice(20000);
            product.setCreatedTime(new Date());
            product.setUpdatedTime(new Date());
            product.setInStock(true);
            product.setEnabled(true);

            Product save = repo.save(product);
        }
    }

    @Test
    public void testListAllProducts() {
        Iterable<Product> findAll = repo.findAll();

        for (Product product : findAll) {
            System.out.println(product);
        }
    }

    @Test
    public void testGetProduct() {
        Product product = repo.findById(2).get();
        assertThat(product).isNotNull();
    }

    @Test
    public void testUpdateProduct() {
        Product product = repo.findById(1).get();
        product.setEnabled(true);
        product.setInStock(true);
        product.setPrice(15000);
        repo.save(product);

    }

    @Test
    public void testUpdateStatus() {
        repo.updateStatus(1, false);
    }

    @Test
    public void testSaveProductWithImages() {
        Integer productId = 1;
        Product product = repo.findById(productId).get();
        product.setMainImage("main image.jpg");
        product.addExtraImage("extra image 1.png");
        product.addExtraImage("extra image 2.png");
        product.addExtraImage("extra image 3.png");
        Product save = repo.save(product);
        assertThat(save.getImages().size()).isEqualTo(3);
    }

    @Test
    public void testSaveProductWithDetails() {
        Integer productId = 1;
        Product product = repo.findById(productId).get();

        product.addDetail("Bộ nhớ", "12GB");
        product.addDetail("CPU", "i9 10900k");
        product.addDetail("OS", "Windows");
        repo.save(product);
    }

}
