package vn.iotstar.controller.api;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.entity.Category;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IStorageService;

/**
 * REST API cho Category - dung dung convention trong tai lieu:
 * dung @RequestParam + MultipartFile (form-data), khong dung @RequestBody JSON
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/category")
public class CategoryAPIController {

	@Autowired
	private ICategoryService categoryService;

	@Autowired
	private IStorageService storageService;

	/** Lay tat ca danh muc */
	@GetMapping()
	public ResponseEntity<Response> getAllCategories() {
		List<Category> list = categoryService.findAll();
		if (list.isEmpty()) {
			return new ResponseEntity<Response>(
					new Response(true, "Khong co danh muc nao", list), HttpStatus.OK);
		}
		return new ResponseEntity<Response>(
					new Response(true, "Thanh cong", list), HttpStatus.OK);
	}

	/** Lay 1 danh muc theo id */
	@PostMapping("/getCategory")
	public ResponseEntity<Response> getCategory(@RequestParam("id") Long id) {
		Optional<Category> opt = categoryService.findById(id);
		if (opt.isEmpty()) {
			return new ResponseEntity<Response>(
					new Response(false, "Khong tim thay danh muc", null), HttpStatus.OK);
		}
		return new ResponseEntity<Response>(
					new Response(true, "Thanh cong", opt.get()), HttpStatus.OK);
	}

	/** Them moi danh muc - form-data: categoryName, icon(file) */
	@PostMapping("/addCategory")
	public ResponseEntity<Response> addCategory(
			@RequestParam("categoryName") String categoryName,
			@RequestParam(value = "icon", required = false) MultipartFile icon) {
		try {
			Category category = new Category();
			category.setCategoryName(categoryName);

			// Chi luu file khi nguoi dung co chon anh
			if (icon != null && !icon.isEmpty()) {
				category.setIcon(storageService.store(icon));
			}

			categoryService.save(category);
			return new ResponseEntity<Response>(
					new Response(true, "Them danh muc thanh cong", category), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Response>(
					new Response(false, "That bai: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Cap nhat danh muc.
	 * Chap nhan CA PUT VA POST:
	 * - PUT: khi HiddenHttpMethodFilter doi duoc method tu POST + _method=PUT
	 * - POST: du phong neu filter khong hoat dong tren moi truong cua ban
	 * Nho vay giao dien chi can gui POST kem _method=PUT la chay duoc ca 2 truong hop.
	 */
	@RequestMapping(value = "/updateCategory", method = { RequestMethod.PUT, RequestMethod.POST })
	public ResponseEntity<Response> updateCategory(
			@RequestParam("categoryId") Long categoryId,
			@RequestParam("categoryName") String categoryName,
			@RequestParam(value = "icon", required = false) MultipartFile icon) {
		try {
			Optional<Category> opt = categoryService.findById(categoryId);
			if (opt.isEmpty()) {
				return new ResponseEntity<Response>(
						new Response(false, "Khong tim thay danh muc", null), HttpStatus.OK);
			}

			Category category = opt.get();
			category.setCategoryName(categoryName);

			// Chi cap nhat anh khi nguoi dung chon anh moi.
			// Neu khong chon, service.save() se tu giu lai icon cu.
			if (icon != null && !icon.isEmpty()) {
				category.setIcon(storageService.store(icon));
			} else {
				category.setIcon(null);
			}

			categoryService.save(category);
			return new ResponseEntity<Response>(
					new Response(true, "Cap nhat danh muc thanh cong", category), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Response>(
					new Response(false, "That bai: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/** Xoa danh muc theo id */
	@DeleteMapping("/deleteCategory")
	public ResponseEntity<Response> deleteCategory(@RequestParam("categoryId") Long categoryId) {
		try {
			Optional<Category> opt = categoryService.findById(categoryId);
			if (opt.isEmpty()) {
				return new ResponseEntity<Response>(
						new Response(false, "Khong tim thay danh muc", null), HttpStatus.OK);
			}
			categoryService.deleteById(categoryId);
			return new ResponseEntity<Response>(
					new Response(true, "Xoa danh muc thanh cong", null), HttpStatus.OK);
		} catch (Exception e) {
			// Thuong gap: danh muc con san pham -> vi pham khoa ngoai
			return new ResponseEntity<Response>(
					new Response(false, "Khong xoa duoc, co the danh muc dang chua san pham", null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
