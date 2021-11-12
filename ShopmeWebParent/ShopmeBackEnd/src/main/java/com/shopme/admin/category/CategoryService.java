package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repo;

    private static final int ROOT_CATEGORIES_PER_PAGE = 4;

    public List<Category> listAll(String sortDir) {
	Sort sort = Sort.by("name");
	if (sortDir == null || sortDir.isEmpty()) {
	    sort = sort.ascending();
	} else if (sortDir.equals("asc")) {
	    sort = sort.ascending();
	} else if (sortDir.equals("desc")) {
	    sort = sort.descending();
	} else {
	    sort = sort.ascending();
	}
	List<Category> rootCategories = repo.findRootCategories(sort);
	return listHierarchicalCategories(rootCategories, sortDir);
    }

    public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
	Sort sort = Sort.by("name");
	if (sortDir == null || sortDir.isEmpty()) {
	    sort = sort.ascending();
	} else if (sortDir.equals("asc")) {
	    sort = sort.ascending();
	} else if (sortDir.equals("desc")) {
	    sort = sort.descending();
	} else {
	    sort = sort.ascending();
	}

	Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE);
	Page<Category> pageCategory = null;
	if (keyword != null && !keyword.isEmpty()) {
	    pageCategory = repo.search(keyword, pageable);
	} else {
	    pageCategory = repo.findRootCategories(pageable);
	}

	List<Category> rootCategory = pageCategory.getContent();

	pageInfo.setTotalElements(pageCategory.getTotalElements());
	pageInfo.setTotalPage(pageCategory.getTotalPages());
	if (keyword != null && !keyword.isEmpty()) {
	    List<Category> searchResult = pageCategory.getContent();
	    for (Category category : searchResult) {
		category.setHasChildren(category.getChildrent().size() > 0);
	    }
	    return searchResult;
	} else {
	    return listHierarchicalCategories(rootCategory, sortDir);
	}
    }

    public Category get(Integer id) throws Exception {
	try {
	    return repo.findById(id).get();
	} catch (Exception e) {
	    throw new Exception("Khong tim thay category co ID: " + id);
	}
    }

    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
	List<Category> hierarchicalCategories = new ArrayList<>();
	if (sortDir == null || sortDir.isEmpty()) {
	    sortDir = "asc";
	}
	for (Category rootCategory : rootCategories) {
	    hierarchicalCategories.add(Category.copyFull(rootCategory));

	    Set<Category> childrent = sortSubCategories(rootCategory.getChildrent(), sortDir);

	    for (Category subCategory : childrent) {
		String name = "--" + subCategory.getName();
		hierarchicalCategories.add(Category.copyFull(subCategory, name));

		listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
	    }
	}

	return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel,
	    String sortDir) {
	if (sortDir == null || sortDir.isEmpty()) {
	    sortDir = "asc";
	}
	Set<Category> childrent = sortSubCategories(parent.getChildrent(), sortDir);
	int newSubLevel = subLevel + 1;
	for (Category subCategory : childrent) {
	    String name = "";
	    for (int i = 0; i < newSubLevel; i++) {
		name += "--";
	    }
	    name += subCategory.getName();

	    hierarchicalCategories.add(Category.copyFull(subCategory, name));

	    listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
	}
    }

    public Category save(Category category) {
	return repo.save(category);
    }

    public List<Category> listCategoryUsedInForm() {
	List<Category> categoriesUsedInForm = new ArrayList<>();

	Iterable<Category> categoriesInDB = repo.findRootCategories(Sort.by("name").ascending());

	for (Category category : categoriesInDB) {
	    if (category.getParent() == null) {
		categoriesUsedInForm.add(Category.copyIdAndName(category));

		Set<Category> childrent = sortSubCategories(category.getChildrent());

		for (Category subCate : childrent) {
		    String name = "--" + subCate.getName();
		    categoriesUsedInForm.add(Category.copyIdAndName(subCate.getId(), name));
		    listSubCategoriesUsedInForm(categoriesUsedInForm, subCate, 1);
		}
	    }
	}

	return categoriesUsedInForm;
    }

    private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
	int newSubLevel = subLevel + 1;

	for (Category subCategory : sortSubCategories(parent.getChildrent())) {
	    String name = "";
	    for (int i = 0; i < newSubLevel; i++) {
		name += "--";
	    }
	    name += subCategory.getName();
	    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
	    listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
	}
    }

    public String checkUnique(Integer id, String name, String alias) {
	boolean isCreatingNew = (id == null || id == 0);
	Category findByName = repo.findByName(name);
	if (isCreatingNew) {
	    if (findByName != null) {
		return "DuplicateName";
	    } else {
		Category findByAlias = repo.findByAlias(alias);
		if (findByAlias != null) {
		    return "DuplicateAlias";
		}
	    }
	} else {
	    if (findByName != null && findByName.getId() != id) {
		return "DuplicateName";
	    }
	    Category findByAlias = repo.findByAlias(alias);
	    if (findByAlias != null && findByAlias.getId() != id) {
		return "DuplicateAlias";
	    }
	}

	return "OK";
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
	return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {

	SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

	    @Override
	    public int compare(Category o1, Category o2) {

		if (sortDir.equals("asc")) {
		    return o1.getName().compareTo(o2.getName());
		} else {
		    return o2.getName().compareTo(o1.getName());
		}
	    }
	});
	sortedChildren.addAll(children);
	return sortedChildren;
    }

    public void updateEnableStatus(Integer id, boolean status) {
	repo.updateEnableStatus(id, status);
    }

    public void delete(Integer id) throws Exception {
	Long countById = repo.countById(id);
	if (countById == null || countById == 0) {
	    throw new Exception("Khong tim thay category co ID: " + id);
	}
	repo.deleteById(id);
    }
}
