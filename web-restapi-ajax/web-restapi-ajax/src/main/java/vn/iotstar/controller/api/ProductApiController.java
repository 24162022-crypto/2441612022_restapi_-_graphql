package vn.iotstar.controller.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import vn.iotstar.entity.Product;
import vn.iotstar.model.ProductModel;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IStorageService;

/**
 * REST API cho Product - lam day du CRUD tuong tu Category.
 *
 * === GHI CHU SUA LOI CODE MAU CUA GIANG VIEN ===
 * Code mau addProduct trong tai lieu tao "ProductModel proModel = new ProductModel()"
 * RONG roi copy sang entity bang BeanUtils. Vi proModel chua duoc gan du lieu tu
 * cac @RequestParam nen entity luon rong -> luu xuong DB bi null/0 het.
 * O day da sua: nhan truc tiep cac @RequestParam va set thang vao entity Product.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/product")
public class ProductApiController {

	@Autowired
	private IProductService productService;

	@Autowired
	private ICategoryService categoryService;

	@Autowired
	private IStorageService storageService;

	/**
	 * Chuyen entity Product sang ProductModel de tra ve cho client.
	 * Can thiet vi entity co @JsonIgnore tren field category.
	 */
	private ProductModel toModel(Product p) {
		ProductModel m = new ProductModel();
		m.setProductId(p.getProductId());
		m.setProductName(p.getProductName());
		m.setQuantity(p.getQuantity());
		m.setUnitPrice(p.getUnitPrice());
		m.setImages(p.getImages());
		m.setDescription(p.getDescription());
		m.setDiscount(p.getDiscount());
		m.setCreateDate(p.getCreateDate());
		m.setStatus(p.getStatus());
		// Doc category NGAY TRONG request (con session) -> tranh LazyInitializationException
		if (p.getCategory() != null) {
			m.setCategoryId(p.getCategory().getCategoryId());
			m.setCategoryName(p.getCategory().getCategoryName());
		}
		return m;
	}

	private List<ProductModel> toModelList(List<Product> list) {
		List<ProductModel> result = new ArrayList<ProductModel>();
		for (Product p : list) {
			result.add(toModel(p));
		}
		return result;
	}

	/** Lay tat ca san pham */
	@GetMapping()
	public ResponseEntity<Response> getAllProducts() {
		List<Product> list = productService.findAll();
		return new ResponseEntity<Response>(
				new Response(true, "Thanh cong", toModelList(list)), HttpStatus.OK);
	}

	/** Lay 1 san pham theo id */
	@PostMapping("/getProduct")
	public ResponseEntity<Response> getProduct(@RequestParam("id") Long id) {
		Optional<Product> opt = productService.findById(id);
		if (opt.isEmpty()) {
			return new ResponseEntity<Response>(
					new Response(false, "Khong tim thay san pham", null), HttpStatus.OK);
		}
		return new ResponseEntity<Response>(
				new Response(true, "Thanh cong", toModel(opt.get())), HttpStatus.OK);
	}

	/**
	 * Them moi san pham - form-data.
	 * DA SUA so voi code mau: set truc tiep tu @RequestParam vao entity.
	 */
	@PostMapping("/addProduct")
	public ResponseEntity<Response> addProduct(
			@RequestParam("productName") String productName,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
			@RequestParam("unitPrice") double unitPrice,
			@RequestParam("discount") double discount,
			@RequestParam("description") String description,
			@RequestParam("categoryId") Long categoryId,
			@RequestParam("quantity") int quantity,
			@RequestParam("status") short status) {
		try {
			Optional<Category> optCategory = categoryService.findById(categoryId);
			if (optCategory.isEmpty()) {
				return new ResponseEntity<Response>(
						new Response(false, "Danh muc khong ton tai", null), HttpStatus.OK);
			}

			Product product = new Product();
			product.setProductName(productName);
			product.setUnitPrice(unitPrice);
			product.setDiscount(discount);
			product.setDescription(description);
			product.setQuantity(quantity);
			product.setStatus(status);
			product.setCategory(optCategory.get());
			product.setCreateDate(new Date());

			if (imageFile != null && !imageFile.isEmpty()) {
				product.setImages(storageService.store(imageFile));
			}

			productService.save(product);
			return new ResponseEntity<Response>(
					new Response(true, "Them san pham thanh cong", toModel(product)), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Response>(
					new Response(false, "That bai: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Cap nhat san pham.
	 * Chap nhan ca PUT va POST (xem giai thich o CategoryAPIController).
	 */
	@RequestMapping(value = "/updateProduct", method = { RequestMethod.PUT, RequestMethod.POST })
	public ResponseEntity<Response> updateProduct(
			@RequestParam("productId") Long productId,
			@RequestParam("productName") String productName,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
			@RequestParam("unitPrice") double unitPrice,
			@RequestParam("discount") double discount,
			@RequestParam("description") String description,
			@RequestParam("categoryId") Long categoryId,
			@RequestParam("quantity") int quantity,
			@RequestParam("status") short status) {
		try {
			Optional<Product> opt = productService.findById(productId);
			if (opt.isEmpty()) {
				return new ResponseEntity<Response>(
						new Response(false, "Khong tim thay san pham", null), HttpStatus.OK);
			}

			Optional<Category> optCategory = categoryService.findById(categoryId);
			if (optCategory.isEmpty()) {
				return new ResponseEntity<Response>(
						new Response(false, "Danh muc khong ton tai", null), HttpStatus.OK);
			}

			Product product = opt.get();
			product.setProductName(productName);
			product.setUnitPrice(unitPrice);
			product.setDiscount(discount);
			product.setDescription(description);
			product.setQuantity(quantity);
			product.setStatus(status);
			product.setCategory(optCategory.get());

			// Co anh moi thi luu, khong co thi de null de service giu anh cu
			if (imageFile != null && !imageFile.isEmpty()) {
				product.setImages(storageService.store(imageFile));
			} else {
				product.setImages(null);
			}

			productService.save(product);
			return new ResponseEntity<Response>(
					new Response(true, "Cap nhat san pham thanh cong", toModel(product)), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Response>(
					new Response(false, "That bai: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/** Xoa san pham theo id */
	@DeleteMapping("/deleteProduct")
	public ResponseEntity<Response> deleteProduct(@RequestParam("productId") Long productId) {
		try {
			Optional<Product> opt = productService.findById(productId);
			if (opt.isEmpty()) {
				return new ResponseEntity<Response>(
						new Response(false, "Khong tim thay san pham", null), HttpStatus.OK);
			}
			productService.deleteById(productId);
			return new ResponseEntity<Response>(
					new Response(true, "Xoa san pham thanh cong", null), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Response>(
					new Response(false, "That bai: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Tim kiem san pham co phan trang.
	 * Tra ve Response.body la 1 Map gom: content, totalPages, currentPage, totalItems
	 */
	@GetMapping("/search")
	public ResponseEntity<Response> searchProduct(
			@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "5") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<Product> pageProduct = productService.findByProductNameContaining(keyword, pageable);

			Map<String, Object> body = new HashMap<String, Object>();
			body.put("content", toModelList(pageProduct.getContent()));
			body.put("totalPages", pageProduct.getTotalPages());
			body.put("currentPage", pageProduct.getNumber());
			body.put("totalItems", pageProduct.getTotalElements());

			return new ResponseEntity<Response>(
					new Response(true, "Thanh cong", body), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Response>(
					new Response(false, "That bai: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
