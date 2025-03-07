package com.restaurant.restimpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.rest.DashboardRest;
import com.restaurant.service.DashboardService;

@RestController
public class DashboardRestImpl implements DashboardRest {

	@Autowired
	DashboardService dashboardService;
	
	@Override
	public ResponseEntity<Map<String, Object>> getCount() {
		return dashboardService.getCount();
	}

}
