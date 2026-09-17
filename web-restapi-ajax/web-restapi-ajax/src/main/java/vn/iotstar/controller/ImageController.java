package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import vn.iotstar.exception.StorageFileNotFoundException;
import vn.iotstar.service.IStorageService;

/**
 * Tra ve file anh cho trinh duyet.
 * Vi du: <img src="/admin/products/images/pabc123.png">
 */
@RestController
public class ImageController {

	@Autowired
	private IStorageService storageService;

	@GetMapping("/admin/products/images/{filename:.+}")
	public ResponseEntity<Resource> serveProductImage(@PathVariable String filename) {
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@GetMapping("/admin/categories/images/{filename:.+}")
	public ResponseEntity<Resource> serveCategoryImage(@PathVariable String filename) {
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	// Khong tim thay anh -> tra 404 thay vi 500
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}
