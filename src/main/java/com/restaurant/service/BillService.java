package com.restaurant.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.restaurant.model.Bill;

public interface BillService {

	ResponseEntity<String> generateReport(Map<String, Object> requestMap);

	ResponseEntity<List<Bill>> getBills();

	ResponseEntity<Byte[]> getPdf(Map<String, Object> requestMap);

	ResponseEntity<String> deleteBill(Integer id);

}
