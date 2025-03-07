package com.restaurant.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.restaurant.model.Category;

@RequestMapping(path = "/category")
public interface CategoryRest {

	@PostMapping("/add")
	ResponseEntity<String> addNewCategory(@RequestBody Map<String, String> requestMap);

	@GetMapping(path = "/get")
	ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false) String filterValue);
	
	@PostMapping(path = "/update")
	ResponseEntity<String> updateCategory(@RequestBody Map<String, String> requestMap);
	
	@PostMapping(path = "/delete")
	ResponseEntity<String> deleteCategory(@RequestBody Map<String, String> requestMap);
}
