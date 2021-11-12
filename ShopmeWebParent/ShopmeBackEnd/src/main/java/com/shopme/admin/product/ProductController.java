package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brands.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;

@Controller
@Transactional
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @GetMapping("/products")
    public String listAll(Model model) {
	List<Product> listProducts = productService.listAll();
	model.addAttribute("listProducts", listProducts);
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
	    @RequestParam(name = "detailNames", required = false) String[] detailNames,
	    @RequestParam(name = "detailValues", required = false) String[] detailValues) throws IOException {
	setMainImageName(mainImageMultipart, product);
	setExtraImageNames(extraImageMultipart, product);
	setProductDetails(detailNames, detailValues, product);
	Product save = productService.save(product);
	saveUploadedImages(mainImageMultipart, extraImageMultipart, save);
	attributes.addFlashAttribute("message", "Đã lưu product thành công");
	return "redirect:/products";
    }

    private void setProductDetails(String[] detailNames, String[] detailValues, Product product) {
	// TODO Auto-generated method stub
	if (detailNames == null || detailNames.length == 0) {
	    return;
	}
	for (int i = 0; i < detailNames.length; i++) {
	    String name = detailNames[i];
	    String value = detailValues[i];
	    if (!name.isEmpty() && !value.isEmpty()) {
		product.addDetail(name, value);
	    }
	}
    }

    private void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultipart,
	    Product product) throws IOException {
	// TODO Auto-generated method stub
	if (!mainImageMultipart.isEmpty()) {
	    String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
	    String uploadDir = "../product-images/" + product.getId();
	    FileUploadUtil.cleanDir(uploadDir);
	    FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
	}

	if (extraImageMultipart.length > 0) {
	    String uploadDir = "../product-images/" + product.getId() + "/extras";
	    for (MultipartFile multipartFile : extraImageMultipart) {
		if (multipartFile.isEmpty()) {
		    continue;
		}
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	    }
	}
    }

    private void setExtraImageNames(MultipartFile[] extraImageMultipart, Product product) {
	// TODO Auto-generated method stub
	if (extraImageMultipart.length > 0) {
	    for (MultipartFile multipartFile : extraImageMultipart) {
		if (!multipartFile.isEmpty()) {
		    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		    product.addExtraImage(fileName);
		}
	    }
	}
    }

    private void setMainImageName(MultipartFile mainImageMultipart, Product product) {
	// TODO Auto-generated method stub
	if (!mainImageMultipart.isEmpty()) {
	    String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
	    product.setMainImage(fileName);
	}
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
}
