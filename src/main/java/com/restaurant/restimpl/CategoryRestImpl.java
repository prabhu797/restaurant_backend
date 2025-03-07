package com.restaurant.restimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.constants.RestaurantConstants;
import com.restaurant.model.Category;
import com.restaurant.rest.CategoryRest;
import com.restaurant.service.CategoryService;
import com.restaurant.utils.RestaurantUtils;

@RestController
public class CategoryRestImpl implements CategoryRest {

	@Autowired
	CategoryService categoryService;

	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
		try {
			return categoryService.addNewCategory(requestMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		try {
			return categoryService.getAllCategory(filterValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {
			return categoryService.updateCategory(requestMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> deleteCategory(Map<String, String> requestMap) {
		try {
			return categoryService.deleteCategory(requestMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
