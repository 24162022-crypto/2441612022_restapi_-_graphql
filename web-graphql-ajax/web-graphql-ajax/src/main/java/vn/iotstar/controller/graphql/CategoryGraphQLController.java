package vn.iotstar.controller.graphql;

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
import vn.iotstar.model.CategoryInput;
import vn.iotstar.model.DeleteResult;
import vn.iotstar.model.PageResult;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;

/**
 * Controller GraphQL cho Category.
 * Field "products" duoc giai quyet bang @SchemaMapping de tranh
 * LazyInitializationException va vong lap Category -> Product -> Category.
 */
@Controller
public class CategoryGraphQLController {

	@Autowired
	private ICategoryService categoryService;

	@Autowired
	private IProductService productService;

	// ==================== QUERY ====================

	@QueryMapping
	public List<Category> allCategories() {
		return categoryService.findAll();
	}

	@QueryMapping
	public PageResult<Category> searchCategories(@Argument String keyword,
	                                             @Argument Integer page,
	                                             @Argument Integer size) {
		String kw = (keyword == null) ? "" : keyword;
		int p = (page == null || page < 0) ? 0 : page;
		int s = (size == null || size <= 0) ? 5 : size;

		Pageable pageable = PageRequest.of(p, s);
		Page<Category> result = categoryService.findByCategoryNameContaining(kw, pageable);

		return new PageResult<Category>(
				result.getContent(),
				result.getTotalPages(),
				result.getTotalElements(),
				result.getNumber());
	}

	@QueryMapping
	public Category categoryById(@Argument Long id) {
		return categoryService.findById(id).orElse(null);
	}

	// ==================== MUTATION ====================

	@MutationMapping
	public Category createCategory(@Argument CategoryInput input) {
		Category category = new Category();
		category.setCategoryName(input.getCategoryName());
		category.setIcon(input.getIcon());
		return categoryService.save(category);
	}

	@MutationMapping
	public Category updateCategory(@Argument Long id, @Argument CategoryInput input) {
		Optional<Category> opt = categoryService.findById(id);
		if (opt.isEmpty()) {
			throw new IllegalArgumentException("Khong tim thay danh muc: " + id);
		}
		Category category = opt.get();
		category.setCategoryName(input.getCategoryName());
		// icon rong -> service.save() giu lai icon cu
		category.setIcon(input.getIcon());
		return categoryService.save(category);
	}

	@MutationMapping
	public DeleteResult deleteCategory(@Argument Long id) {
		Optional<Category> opt = categoryService.findById(id);
		if (opt.isEmpty()) {
			return new DeleteResult(false, "Khong tim thay danh muc");
		}
		try {
			categoryService.deleteById(id);
			return new DeleteResult(true, "Xoa danh muc thanh cong");
		} catch (Exception e) {
			return new DeleteResult(false, "Khong xoa duoc, co the danh muc dang chua san pham");
		}
	}

	// ==================== SCHEMA MAPPING ====================

	/**
	 * Giai quyet field "products" cua type Category.
	 * Truy van lai tu ProductRepository thay vi doc collection lazy cua entity.
	 */
	@SchemaMapping(typeName = "Category", field = "products")
	@Transactional(readOnly = true)
	public List<Product> products(Category category) {
		return productService.findByCategoryId(category.getCategoryId());
	}
}
