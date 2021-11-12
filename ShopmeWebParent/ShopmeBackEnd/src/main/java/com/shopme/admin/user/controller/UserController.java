package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

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
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("/users")
    public String listFirstPage(Model model) {
	return listByPage(1, "firstName", "asc", null, model);
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, @Param("sortField") String sortField,
	    @Param("sortDir") String sortDir, @Param("keyword") String keyword, Model model) {
	Page<User> pageUser = service.listByPage(pageNum, sortField, sortDir, keyword);
	List<User> listUsers = pageUser.getContent();

	long startCount = (pageNum - 1) * service.USERS_PER_PAGE + 1;
	long endCount = startCount + service.USERS_PER_PAGE - 1;
	if (endCount > pageUser.getTotalElements()) {
	    endCount = pageUser.getTotalElements();
	}

	String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

	model.addAttribute("currentPage", pageNum);
	model.addAttribute("totalPages", pageUser.getTotalPages());
	model.addAttribute("startCount", startCount);
	model.addAttribute("endCount", endCount);
	model.addAttribute("totalItems", pageUser.getTotalElements());
	model.addAttribute("listUsersSize", listUsers.size());
	model.addAttribute("listUsers", listUsers);
	model.addAttribute("sortField", sortField);
	model.addAttribute("keyword", keyword);
	model.addAttribute("sortDir", sortDir);
	model.addAttribute("reverseSortDir", reverseSortDir);
	return "users/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
	List<Role> listRoles = service.listRoles();

	User user = new User();
	user.setEmail("admin@gmail.com");
	user.setFirstName("Thao");
	user.setLastName("Trang");
	user.setPassword("123456");
	Role assistant = new Role(5);
	user.addRole(assistant);
	user.setEnable(true);
	model.addAttribute("user", user);
	model.addAttribute("pageTitle", "Create New User");
	model.addAttribute("listRoles", listRoles);

	return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
	    @RequestParam("imagePhoto") MultipartFile multipartFile) throws IOException {
	if (!multipartFile.isEmpty()) {
	    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	    user.setPhotos(fileName);
	    User savedUser = service.save(user);
	    String uploadDir = "user-photo/" + savedUser.getId();
	    FileUploadUtil.cleanDir(uploadDir);
	    FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	} else {
	    if (user.getPhotos().isEmpty())
		user.setPhotos(null);
	    service.save(user);
	}
	redirectAttributes.addFlashAttribute("message", "The user has been saved");
	return getRedirectURLtoAffectedUser(user);
    }

    private String getRedirectURLtoAffectedUser(User user) {
	String firstPartOfEmail = user.getEmail().split("@")[0];
	return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
	try {
	    List<Role> listRoles = service.listRoles();
	    User user = service.get(id);

	    model.addAttribute("user", user);
	    model.addAttribute("listRoles", listRoles);
	    model.addAttribute("pageTitle", "Edit User (ID: " + user.getId() + ")");

	    return "users/user_form";
	} catch (UserNotFoundException e) {
	    redirectAttributes.addFlashAttribute("message1", e.getMessage());
	    return "redirect:/users";
	}
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
	try {
	    service.delete(id);
	    redirectAttributes.addFlashAttribute("message", "The User ID " + id + " has been deleted successfully");
	} catch (UserNotFoundException e) {
	    redirectAttributes.addFlashAttribute("message1", e.getMessage());
	}
	return "redirect:/users";
    }

    @GetMapping("users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
	    RedirectAttributes redirectAttributes) {
	service.updateUserStatus(id, status);
	redirectAttributes.addFlashAttribute("message",
		"The user ID " + id + " has been " + (status ? "enabled" : "disable"));
	return "redirect:/users";
    }
}