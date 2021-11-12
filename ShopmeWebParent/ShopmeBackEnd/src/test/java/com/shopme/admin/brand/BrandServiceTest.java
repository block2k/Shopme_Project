package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.admin.brands.BrandRepository;
import com.shopme.admin.brands.BrandService;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BrandServiceTest {
    
    @MockBean
    private BrandRepository repository;
    
    @InjectMocks
    private BrandService service;
    
    @Test
    public void testCheckUnique() {
	String check = service.checkUnique(null, "Acer");
	assertThat(check).isEqualTo("OK");
	System.out.println(check);
    }
}
