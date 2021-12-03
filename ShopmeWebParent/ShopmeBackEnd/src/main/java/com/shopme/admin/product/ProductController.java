package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brands.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@Transactional
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDetailService productDetailService;

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return listByPage(1, null, "name", "asc", 0, model);
    }

    @GetMapping("products/page/{pageNum}")
    public String listByPage(@PathVariable("pageNum") int pageNum,
                             @Param("keyword") String keyword,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("categoryId") Integer categoryId,
                             Model model) {
        System.out.println("category id: " + categoryId);
        if (sortField == null)
            sortField = "name";
        if (sortDir == null) {
            sortDir = "asc";
        }
        Page<Product> page = productService.listByPage(pageNum, keyword, sortField, sortDir, categoryId);
        List<Product> listProducts = page.getContent();
        List<Category> listCategories = categoryService.listCategoryUsedInForm();
        long startCount = (pageNum - 1) * ProductService.PRODUCS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        if (categoryId != null) {
            model.addAttribute("categoryId", categoryId);
        }
        model.addAttribute("sortDirReverse", reverseSortDir);
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listProductsSize", listProducts.size());
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> listBrands = brandService.listAll();
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create new product");

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes attributes,
                              @RequestParam("fileImage") MultipartFile mainImageMultipart,
                              @RequestParam("extraImage") MultipartFile[] extraImageMultipart,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIds", required = false) String[] imageIds,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames) throws IOException {
        ProductSaveHelper.setMainImageName(mainImageMultipart, product);
        ProductSaveHelper.setExistingExtraImageNames(imageIds, imageNames, product);
        ProductSaveHelper.setNewExtraImageNames(extraImageMultipart, product);
        if (product.getId() != null) {
            productDetailService.deleteProductDetailsByProductId(product.getId());
        }
        ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);
        Product save = productService.save(product);
        ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultipart, save);
        ProductSaveHelper.deleteExtraImagesWeredRemovedOnForm(product);
        attributes.addFlashAttribute("message", "Đã lưu product thành công");
        return "redirect:/products";
    }


    @GetMapping("/products/enabled/{id}/{status}")
    public String updateStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean status) {
        productService.updateStatus(id, status);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes attributes) {
        productService.deleteProduct(id);
        String uploadDir = "../product-images/" + id + "/extras";
        String uploadDir2 = "../product-images/" + id;
        FileUploadUtil.removeDir(uploadDir);
        FileUploadUtil.removeDir(uploadDir2);

        attributes.addFlashAttribute("message", "Đã xoá product có ID (" + id + ")");
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
        try {
            Product product = productService.get(id);
            List<Brand> listBrands = brandService.listAll();
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "Edit Product ID: " + id);
            return "products/product_form";
        } catch (Exception e) {
            attributes.addFlashAttribute("message1", e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/products";
    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetail(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);
            return "products/product_detail_modal";
        } catch (Exception e) {
            attributes.addFlashAttribute("message1", e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/products";
    }
}
