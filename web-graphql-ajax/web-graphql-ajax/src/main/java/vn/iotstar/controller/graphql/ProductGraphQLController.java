package vn.iotstar.controller.graphql;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.model.DeleteResult;
import vn.iotstar.model.PageResult;
import vn.iotstar.model.ProductInput;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;

/**
 * Controller GraphQL cho Product.
 *
 * XU LY LAZY LOADING / VONG LAP VO HAN:
 * - Entity Product co quan he @ManyToOne toi Category, va Category co @OneToMany
 *   nguoc lai. Neu de GraphQL tu doc field "category" luc serialize thi de dinh
 *   LazyInitializationException (session da dong).
 * - Giai phap: khai bao @SchemaMapping cho field "category". Ham nay chi chay
 *   KHI client thuc su hoi field do, va chay trong 1 transaction rieng -> vua
 *   tranh lazy loading, vua khong bi lap vo han (GraphQL chi tra dung do sau ma
 *   client yeu cau, khac voi Jackson serialize toan bo cay).
 */
@Controller
public class ProductGraphQLController {

	@Autowired
	private IProductService productService;

	@Autowired
	private ICategoryService categoryService;

	// ==================== QUERY ====================

	/** Tat ca san pham sap xep gia tang dan */
	@QueryMapping
	public List<Product> allProductsSortedByPrice() {
		return productService.findAllByOrderByUnitPriceAsc();
	}

	/** San pham theo danh muc */
	@QueryMapping
	public List<Product> productsByCategory(@Argument Long categoryId) {
		return productService.findByCategoryId(categoryId);
	}

	/** Tim kiem san pham co phan trang */
	@QueryMapping
	public PageResult<Product> searchProducts(@Argument String keyword,
	                                          @Argument Integer page,
	                                          @Argument Integer size) {
		String kw = (keyword == null) ? "" : keyword;
		int p = (page == null || page < 0) ? 0 : page;
		int s = (size == null || size <= 0) ? 5 : size;

		Pageable pageable = PageRequest.of(p, s);
		Page<Product> result = productService.findByProductNameContaining(kw, pageable);

		return new PageResult<Product>(
				result.getContent(),
				result.getTotalPages(),
				result.getTotalElements(),
				result.getNumber());
	}

	/** Lay 1 san pham theo id */
	@QueryMapping
	public Product productById(@Argument Long id) {
		return productService.findById(id).orElse(null);
	}

	// ==================== MUTATION ====================

	@MutationMapping
	public Product createProduct(@Argument ProductInput input) {
		Optional<Category> optCategory = categoryService.findById(input.getCategoryId());
		if (optCategory.isEmpty()) {
			throw new IllegalArgumentException("Danh muc khong ton tai: " + input.getCategoryId());
		}

		Product product = new Product();
		product.setProductName(input.getProductName());
		product.setQuantity(input.getQuantity());
		product.setUnitPrice(input.getUnitPrice());
		product.setImages(input.getImages());
		product.setDescription(input.getDescription());
		product.setDiscount(input.getDiscount());
		product.setStatus(input.getStatus());
		product.setCategory(optCategory.get());
		product.setCreateDate(new Date());

		return productService.save(product);
	}

	@MutationMapping
	public Product updateProduct(@Argument Long id, @Argument ProductInput input) {
		Optional<Product> opt = productService.findById(id);
		if (opt.isEmpty()) {
			throw new IllegalArgumentException("Khong tim thay san pham: " + id);
		}
		Optional<Category> optCategory = categoryService.findById(input.getCategoryId());
		if (optCategory.isEmpty()) {
			throw new IllegalArgumentException("Danh muc khong ton tai: " + input.getCategoryId());
		}

		Product product = opt.get();
		product.setProductName(input.getProductName());
		product.setQuantity(input.getQuantity());
		product.setUnitPrice(input.getUnitPrice());
		// input.images rong -> service.save() se tu giu lai anh cu
		product.setImages(input.getImages());
		product.setDescription(input.getDescription());
		product.setDiscount(input.getDiscount());
		product.setStatus(input.getStatus());
		product.setCategory(optCategory.get());

		return productService.save(product);
	}

	@MutationMapping
	public DeleteResult deleteProduct(@Argument Long id) {
		Optional<Product> opt = productService.findById(id);
		if (opt.isEmpty()) {
			return new DeleteResult(false, "Khong tim thay san pham");
		}
		productService.deleteById(id);
		return new DeleteResult(true, "Xoa san pham thanh cong");
	}

	// ==================== SCHEMA MAPPING ====================

	/**
	 * Giai quyet field "category" cua type Product.
	 * Chi chay khi client hoi toi field nay -> tranh truy van thua.
	 */
	@SchemaMapping(typeName = "Product", field = "category")
	@Transactional(readOnly = true)
	public Category category(Product product) {
		if (product.getCategory() == null) {
			return null;
		}
		// Nap lai tu DB de chac chan khong dinh lazy proxy chua khoi tao
		return categoryService.findById(product.getCategory().getCategoryId()).orElse(null);
	}

	/**
	 * Giai quyet field "createDate": chuyen Date sang chuoi ISO-8601
	 * vi schema khai bao kieu String.
	 */
	@SchemaMapping(typeName = "Product", field = "createDate")
	public String createDate(Product product) {
		if (product.getCreateDate() == null) {
			return null;
		}
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(product.getCreateDate());
	}
}
