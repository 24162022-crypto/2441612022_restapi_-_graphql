package vn.iotstar.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.model.Response;
import vn.iotstar.service.IStorageService;

/**
 * REST endpoint upload anh dung kem voi GraphQL.
 *
 * VI SAO CAN ENDPOINT NAY?
 * GraphQL chuan (spec) chi truyen du lieu JSON, KHONG ho tro upload file nhi phan.
 * Muon upload qua GraphQL phai dung them spec khong chinh thuc "graphql-multipart-
 * request" cung thu vien ben thu ba, kha phuc tap cho bai tap.
 *
 * Cach lam don gian va pho bien hon (dung o day):
 *   Buoc 1: giao dien POST file len /api/upload (multipart/form-data)
 *           -> server luu file, tra ve TEN FILE vua sinh.
 *   Buoc 2: giao dien goi mutation GraphQL, truyen ten file do vao truong
 *           images (Product) hoac icon (Category).
 */
@CrossOrigin(origins = "*")
@RestController
public class UploadApiController {

	@Autowired
	private IStorageService storageService;

	@PostMapping("/api/upload")
	public ResponseEntity<Response> upload(@RequestParam("file") MultipartFile file) {
		try {
			if (file == null || file.isEmpty()) {
				return new ResponseEntity<Response>(
						new Response(false, "Chua chon file", null), HttpStatus.OK);
			}
			String fileName = storageService.store(file);
			// body chinh la ten file, giao dien se truyen ten nay vao mutation
			return new ResponseEntity<Response>(
					new Response(true, "Upload thanh cong", fileName), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Response>(
					new Response(false, "Upload that bai: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
