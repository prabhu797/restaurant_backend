package com.restaurant.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurant.model.Category;

public interface CategoryDao extends JpaRepository<Category, Integer> {

	List<Category> getAllCategory();
}
