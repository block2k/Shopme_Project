package com.shopme.admin.brands;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Brand;

@Service
@Transactional
public class BrandService {
    @Autowired
    private BrandRepository repo;

    public List<Brand> listAll() {
	return (List<Brand>) repo.findAll();
    }

    public Brand saveBrand(Brand brand) {
	return repo.save(brand);
    }

    public Brand get(Integer id) {
	return repo.findById(id).get();
    }

    public Brand findById(Integer id) throws Exception {
	try {
	    return repo.findById(id).get();
	} catch (Exception e) {
	    throw new Exception("Khong tim thay brand co ID: " + id);
	}
    }

    public void deleteById(Integer id) throws Exception {
	try {
	    repo.deleteById(id);
	} catch (Exception e) {
	    throw new Exception("Khong tim thay brand co ID: " + id);
	}

    }

    public String checkUnique(Integer id, String name) {
	boolean isCreatingNew = false;
	if (id == null || id == 0) {
	    isCreatingNew = true;
	}
	Brand findByName = repo.findByName(name);
	if (isCreatingNew) {
	    if (findByName != null) {
		return "Duplicate";
	    }
	} else {
	    if (findByName != null && findByName.getId() != id) {
		return "Duplicate";
	    }
	}
	return "OK";
    }

    public Page<Brand> listByPage(int pageNum, String keyword, String sortField, String sortDir) {
	Sort sort = Sort.by(sortField);
	if (sortDir == null) {
	    sortDir = "asc";
	}
	sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
	Pageable pageable = PageRequest.of(pageNum - 1, 4, sort);
	if (keyword != null) {
	    return repo.findAll(keyword, pageable);
	} else {
	    return repo.findAll(pageable);
	}
    }
}
