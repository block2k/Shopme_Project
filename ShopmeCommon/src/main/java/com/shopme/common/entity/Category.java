package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 128, nullable = false, unique = true)
    private String name;
    @Column(length = 64, nullable = false, unique = true)
    private String alias;
    @Column(length = 128, nullable = false)
    private String image;
    private boolean enabled;
    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
    @OneToMany(mappedBy = "parent")
    @OrderBy("name asc")
    private Set<Category> childrent = new HashSet<>();

    @Column(name = "all_parent_ids", length = 256, nullable = true)
    private String allParentIds;

    public String getAllParentIds() {
        return allParentIds;
    }

    public void setAllParentIds(String allParentIds) {
        this.allParentIds = allParentIds;
    }

    public Category() {
    }

    public Category(String name) {
	this.name = name;
	this.alias = name;
	this.image = "login.jpg";
    }

    public Category(String name, Category parent) {
	this(name);
	this.parent = parent;
    }

    public Category(Integer id, String name, String alias) {
	this.id = id;
	this.name = name;
	this.alias = alias;
    }

    public static Category copyIdAndName(Category category) {
	Category category2 = new Category();
	category2.setId(category.getId());
	category2.setName(category.getName());

	return category2;
    }

    public static Category copyFull(Category category) {
	Category category2 = new Category();
	category2.setId(category.getId());
	category2.setName(category.getName());
	category2.setImage(category.getImage());
	category2.setAlias(category.getAlias());
	category2.setEnabled(category.isEnabled());
	category2.setHasChildren(category.getChildrent().size() > 0);

	return category2;
    }

    public static Category copyFull(Category category, String name) {
	Category copyCategory = Category.copyFull(category);
	copyCategory.setName(name);
	return copyCategory;
    }

    public static Category copyIdAndName(Integer id, String name) {
	Category category2 = new Category();
	category2.setId(id);
	category2.setName(name);

	return category2;
    }

    public Category(Integer id) {
	this.id = id;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getAlias() {
	return alias;
    }

    public void setAlias(String alias) {
	this.alias = alias;
    }

    public String getImage() {
	return image;
    }

    public void setImage(String image) {
	this.image = image;
    }

    public boolean isEnabled() {
	return enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    public Category getParent() {
	return parent;
    }

    public void setParent(Category parent) {
	this.parent = parent;
    }

    public Set<Category> getChildrent() {
	return childrent;
    }

    public void setChildrent(Set<Category> childrent) {
	this.childrent = childrent;
    }

    @Transient
    public String getImagePath() {
	if (image.isEmpty() || image == null) {
	    return "/images/image-thumbnail.png";
	}
	return "/categories-images/" + this.id + "/" + this.image;
    }

    public boolean isHasChildren() {
	return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
	this.hasChildren = hasChildren;
    }

    @Override
    public String toString() {
	return this.name;
    }

    @Transient
    private boolean hasChildren;

}