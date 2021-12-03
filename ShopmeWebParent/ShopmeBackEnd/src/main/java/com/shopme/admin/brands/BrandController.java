package com.shopme.admin.brands;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
@Transactional
public class BrandController {
    @Autowired
    BrandService service;
    @Autowired
    CategoryService categoryService;

    @GetMapping("/brands")
    public String listAll(Model model) {
        return listByPage(1, null, "name", "asc", model);
    }

    @GetMapping("brands/new")
    public String newBrand(Model model) {
        List<Category> listCategoryUsedInForm = categoryService.listCategoryUsedInForm();
        Brand brand = new Brand();

        model.addAttribute("brand", brand);
        model.addAttribute("listCategories", listCategoryUsedInForm);
        model.addAttribute("pageTitle", "Create new brand");
        return "brands/brand_form";
    }

    @PostMapping("brands/save")
    public String saveBrand(@RequestParam("imagePhoto") MultipartFile multipartFile,
                            RedirectAttributes redirectAttributes, Brand brand) {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);
            Brand saveBrand = service.saveBrand(brand);
            String uploadDir = "../brands-images/" + saveBrand.getId();
            FileUploadUtil.cleanDir(uploadDir);
            try {
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            } catch (IOException e) {

            }
        } else {
            service.saveBrand(brand);
        }
        redirectAttributes.addFlashAttribute("message", "Save thanh cong brand voi ID: " + brand.getId());
        return "redirect:/brands";
    }

    @GetMapping("brands/edit/{id}")
    public String editBrand(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Brand brand = service.findById(id);
            List<Category> listCategoryUsedInForm = categoryService.listCategoryUsedInForm();

            model.addAttribute("chosenCategories", brand.getCategories());
            model.addAttribute("listCategories", listCategoryUsedInForm);
            model.addAttribute("brand", brand);

            return "brands/brand_form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message1", "Khong tim thay brand co ID: " + id);
            return "redirect:/brands";
        }
    }

    @GetMapping("brands/delete/{id}")
    public String deleteBrand(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xoa thanh cong brand co ID: (" + id + ")");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Khong tim thay brand co ID: (" + id + ")");
        }
        return "redirect:/brands";
    }

    @GetMapping("brands/page/{pageNum}")
    public String listByPage(@PathVariable("pageNum") int pageNum,
                             @Param("keyword") String keyword,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             Model model) {
        if (sortField == null)
            sortField = "name";
        Page<Brand> listByPage = service.listByPage(pageNum, keyword, sortField, sortDir);
        List<Brand> content = listByPage.getContent();
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("sortDirReverse", reverseSortDir);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("endPage", listByPage.getTotalPages());
        model.addAttribute("listBrands", content);

        return "brands/brands";
    }
}
