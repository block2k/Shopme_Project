package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;

@Controller
@Transactional
public class CategoryController {
    @Autowired
    private CategoryService service;

    @GetMapping("/categories")
    public String listFirstPage(@Param("sortDir") String sortDir, Model model) {
	return listByPage(1, sortDir, null, model);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable("pageNum") Integer pageNum, @Param("sortDir") String sortDir,
	    @Param("keyword") String keyword, Model model) {
	if (sortDir == null || sortDir.isEmpty()) {
	    sortDir = "asc";
	}

	CategoryPageInfo pageInfo = new CategoryPageInfo();
	List<Category> listCategories = service.listByPage(pageInfo, pageNum, sortDir, keyword);
	String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

	model.addAttribute("totalPages", pageInfo.getTotalPage());
	model.addAttribute("totalItems", pageInfo.getTotalElements());
	model.addAttribute("currentPage", pageNum);
	model.addAttribute("keyword", keyword);

	model.addAttribute("reverseSordir", reverseSortDir);
	model.addAttribute("listCategories", listCategories);

	return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
	List<Category> listCategories = service.listCategoryUsedInForm();

	model.addAttribute("category", new Category());
	model.addAttribute("listCategories", listCategories);
	model.addAttribute("pageTitle", "Create New Category");

	return "categories/category_form";
    }

    @GetMapping("/category/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model,
	    RedirectAttributes redirectAttributes) {
	try {
	    Category category = service.get(id);
	    List<Category> listCategories = service.listCategoryUsedInForm();

	    model.addAttribute("listCategories", listCategories);
	    model.addAttribute("pageTitle", "Edit category ID: " + id);
	    model.addAttribute("category", category);
	} catch (Exception e) {
	    redirectAttributes.addFlashAttribute("message1", "Khong tim thay category co ID: " + id);
	    return "redirect:/categories";
	}
	return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("imagePhoto") MultipartFile multipartFile,
	    RedirectAttributes redirectAttributes) throws IOException {
	if (!multipartFile.isEmpty()) {
	    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	    category.setImage(fileName);
	    Category savedCategory = service.save(category);
	    String uploadDir = "../categories-images/" + savedCategory.getId();
	    FileUploadUtil.cleanDir(uploadDir);
	    FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	} else {
	    service.save(category);
	}
	redirectAttributes.addFlashAttribute("message",
		"The category " + category.getName() + " has been saved successfully.");
	return "redirect:/savedCategory?id=" + category.getId();
    }

    @GetMapping("/savedCategory")
    public String savedCategory(@Param("id") Integer id, Model model) throws Exception {
	try {
	    Category category = service.get(id);
	    model.addAttribute("listCategories", category);
	    return "categories/categories";
	} catch (Exception e) {
	    return "redirect:/categories";
	}
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnableStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
	    RedirectAttributes redirectAttributes) {
	try {
	    service.updateEnableStatus(id, status);

	    redirectAttributes.addFlashAttribute("message",
		    "The ID " + id + " has been update to " + (status ? "enable" : "disable"));
	} catch (Exception e) {
	    redirectAttributes.addFlashAttribute("message1", "The ID " + id + " does not exist!");
	}

	return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
	try {
	    service.delete(id);
	    FileUploadUtil.removeDir("../categories-images" + id);
	    redirectAttributes.addFlashAttribute("message", "Da xoa thanh cong category co ID: " + id);

	} catch (Exception e) {
	    redirectAttributes.addFlashAttribute("message1", "Khong tim thay category co ID: " + id);
	}
	return "redirect:/categories";
    }

}
