package com.shopme.product;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
        return viewCategoryByPage(alias,1,model);
    }

    @GetMapping("/c/{category_alias}/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias,@PathVariable("pageNum")int pageNum, Model model) {
        Category category = categoryService.getCategory(alias);
        if (category == null) {
            return "error/404";
        }
        List<Category> categoryParent = categoryService.getCategoryParent(category);
        //start paging product
        Page<Product> productPage = productService.listByCategory(pageNum, category.getId());
        List<Product> listProducts = productPage.getContent();
        long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
        if (endCount > productPage.getTotalElements()) {
            endCount = productPage.getTotalElements();
        }
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        //end paging product
        model.addAttribute("pageTitle", category.getName());
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("category", category);
        model.addAttribute("listCategoryParent", categoryParent);
        return "product_by_category";
    }
}
