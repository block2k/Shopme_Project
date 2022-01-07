package com.shopme.product;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
        return viewCategoryByPage(alias, 1, model);
    }

    @GetMapping("/c/{category_alias}/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias, @PathVariable("pageNum") int pageNum, Model model) {
        try {
            Category category = categoryService.getCategory(alias);
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
            return "product/product_by_category";
        } catch (CategoryNotFoundException exception) {
            return "error/404";
        }
    }

    @GetMapping("/p/{product_alias}")
    String viewProductDetail(@PathVariable("product_alias") String alias, Model model) {
        try {
            Product product = productService.getProduct(alias);
            List<Category> categoryParent = categoryService.getCategoryParent(product.getCategory());

            model.addAttribute("listCategoryParent", categoryParent);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", product.getShortName());
            return "product/product_detail";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/search")
    String searchFirstPage(@Param("keyword") String keyword, Model model) {
        return searchByPage(keyword, model, 1);
    }

    @GetMapping("/search/page/{pageNum}")
    String searchByPage(@Param("keyword") String keyword, Model model, @PathVariable("pageNum") int pageNum) {
        Page<Product> searchResultPage = productService.search(keyword, pageNum);
        List<Product> products = searchResultPage.getContent();

        long startCount = (pageNum - 1) * ProductService.SEARCH_RESULT_PER_PAGE + 1;
        long endCount = startCount + ProductService.SEARCH_RESULT_PER_PAGE - 1;
        if (endCount > searchResultPage.getTotalElements()) {
            endCount = searchResultPage.getTotalElements();
        }
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", searchResultPage.getTotalPages());
        model.addAttribute("totalItems", searchResultPage.getTotalElements());
        model.addAttribute("pageTitle", keyword + " - Search Result");

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("listProducts", products);
        model.addAttribute("keyword", keyword);
        return "product/search_result";
    }
}
