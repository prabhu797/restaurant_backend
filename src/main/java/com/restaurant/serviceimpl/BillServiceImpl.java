package com.restaurant.serviceimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.restaurant.constants.RestaurantConstants;
import com.restaurant.dao.BillDao;
import com.restaurant.jwt.JwtFilter;
import com.restaurant.model.Bill;
import com.restaurant.service.BillService;
import com.restaurant.utils.RestaurantUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BillServiceImpl implements BillService {

	@Autowired
	BillDao billDao;

	@Autowired
	JwtFilter jwtFilter;

	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		log.info("Inside generateReport {}", requestMap);
		try {
			String filename;
			if (validateRequestMap(requestMap)) {
				if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
					filename = (String) requestMap.get("uuid");
				} else {
					filename = RestaurantUtils.getUUID();
					requestMap.put("uuid", filename);
					insertBill(requestMap);
				}

				String data = "Name: " + requestMap.get("name") + "\n" + "Contact Number: "
						+ requestMap.get("contactNumber") + "\n" + "Email: " + requestMap.get("email") + "\n"
						+ "Payment Method: " + requestMap.get("paymentMethod");

				Document document = new Document();
				PdfWriter.getInstance(document,
						new FileOutputStream(RestaurantConstants.STORE_LOCATION + "\\" + filename + ".pdf"));

				document.open();
				setRectangularInPdf(document);

				Paragraph chunk = new Paragraph("Restaurant Management System", getFont("Header"));
				chunk.setAlignment(Element.ALIGN_CENTER);
				document.add(chunk);

				Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
				document.add(paragraph);

				PdfPTable table = new PdfPTable(5);
				table.setWidthPercentage(100);
				addTableHEader(table);

				JSONArray jsonArray = RestaurantUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));
				for (int i = 0; i < jsonArray.length(); i++) {
					addRows(table, RestaurantUtils.getMapFromJson(jsonArray.getString(i)));
				}

				document.add(table);

				Paragraph footer = new Paragraph("Total: " + requestMap.get("totalAmount") + "\n"
						+ "Thank you for visiting. Please visit again!!", getFont("Data"));

				document.add(footer);
				document.close();
				return new ResponseEntity<String>("{\"uuid\":\"" + filename + "\"}", HttpStatus.OK);

			} else {
				return RestaurantUtils.getResponseEntity("Required data not found.", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG,
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void addRows(PdfPTable table, Map<String, Object> data) {
		log.info("Inside addRows");
		table.addCell((String) data.get("name"));
		table.addCell((String) data.get("category"));
		table.addCell((String) data.get("quantity"));
		table.addCell(Double.toString((Double) data.get("price")));
		table.addCell(Double.toString((Double) data.get("total")));
	}

	private void addTableHEader(PdfPTable table) {
		log.info("Inside addTableHEader");
		Stream.of("Name", "Category", "Quantity", "Price", "Sub Total ").forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(2);
			header.setPhrase(new Phrase(columnTitle));
			header.setBackgroundColor(BaseColor.YELLOW);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			header.setVerticalAlignment(Element.ALIGN_CENTER);
			table.addCell(header);
		});
	}

	private Font getFont(String type) {
		log.info("Inside getInfo");
		switch (type) {
		case "Header":
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
			headerFont.setStyle(Font.BOLD);
			return headerFont;
		case "Data":
			Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
			dataFont.setStyle(Font.BOLD);
			return dataFont;
		default:
			return new Font();
		}
	}

	private void setRectangularInPdf(Document document) throws DocumentException {
		log.info("Inside setRectangularInPdf");
		Rectangle rect = new Rectangle(577, 825, 18, 15);
		rect.enableBorderSide(1);
		rect.enableBorderSide(2);
		rect.enableBorderSide(4);
		rect.enableBorderSide(8);
		rect.setBorderColor(BaseColor.BLACK);
		rect.setBorderWidth(1);
		document.add(rect);
	}

	private Boolean validateRequestMap(Map<String, Object> requestMap) {
		return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
				&& requestMap.containsKey("email") && requestMap.containsKey("paymentMethod")
				&& requestMap.containsKey("productDetails") && requestMap.containsKey("totalAmount");
	}

	private void insertBill(Map<String, Object> requestMap) {
		try {
			Bill bill = new Bill();
			bill.setUuid((String) requestMap.get("uuid"));
			bill.setName((String) requestMap.get("name"));
			bill.setEmail((String) requestMap.get("email"));
			bill.setContactNumber((String) requestMap.get("contactNumber"));
			bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
			bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
			bill.setProductDetails((String) requestMap.get("productDetails"));
			bill.setCraetedBy(jwtFilter.getCurrentUser());
			billDao.save(bill);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public ResponseEntity<List<Bill>> getBills() {
		List<Bill> list = new ArrayList<>();
		try {
			if (jwtFilter.isAdmin()) {
				list = billDao.getAllBills();
			} else {
				list = billDao.getBillByUsername(jwtFilter.getCurrentUser());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<List<Bill>>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Byte[]> getPdf(Map<String, Object> requestMap) {
		log.info("Inside getPdf {}", requestMap);
		try {
			Byte [] byteArray = new Byte[0];
			if(!requestMap.containsKey("uuid") && validateRequestMap(requestMap)) {
				return new ResponseEntity<Byte[]>(byteArray,HttpStatus.BAD_REQUEST);
			}
			String filePath = RestaurantConstants.STORE_LOCATION + "\\" + (String) requestMap.get("uuid") + ".pdf";
			if(RestaurantUtils.isFileExists(filePath)) {
				byteArray = getByteArray(filePath);
				return new ResponseEntity<Byte[]>(byteArray, HttpStatus.OK);
			} else {
				requestMap.put("isGenerate", false);
				generateReport(requestMap);
				byteArray = getByteArray(filePath);
				return new ResponseEntity<Byte[]>(byteArray, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Byte[]>(new Byte[0], HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private Byte[] getByteArray(String filePath) throws Exception {
		File file = new File(filePath);
		InputStream targetStream = new FileInputStream(file);
		byte[] byteArray = IOUtils.toByteArray(targetStream);
		Byte [] byteArrayObject = new Byte[byteArray.length];
		Integer i = 0;
		for(byte b: byteArray) {
			byteArrayObject[i++] = Byte.valueOf(b);
		}
		targetStream.close();
		return byteArrayObject;
	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			Optional<Bill> optional = billDao.findById(id);
			if(!optional.isEmpty()) {
				billDao.deleteById(id);
				return RestaurantUtils.getResponseEntity("Bill deleted successfully.", HttpStatus.OK);
			} else {
				return RestaurantUtils.getResponseEntity("Bill id does not exists.", HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG,
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
