package com.shopme.category;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getEnableCategory() {
        List<Category> listCategoryNoChild = new ArrayList<>();
        List<Category> allEnabled = categoryRepository.findAllEnabled();
        allEnabled.forEach(category -> {
            if (category.getChildrent() == null || category.getChildrent().size() == 0) {
                listCategoryNoChild.add(category);
            }
        });
        return listCategoryNoChild;
    }

    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category category = categoryRepository.findByAliasEnabled(alias);
        if (category == null) {
            throw new CategoryNotFoundException("Category not found");
        }
        return category;
    }

    public List<Category> getCategoryParent(Category child) {
        List<Category> listParent = new ArrayList<>();
        Category parent = child.getParent();
        while (parent != null) {
            listParent.add(0, parent);
            parent = parent.getParent();
        }
        listParent.add(child);
        return listParent;
    }
}
