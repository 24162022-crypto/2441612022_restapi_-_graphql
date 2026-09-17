package vn.iotstar.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lop dung chung cho ProductPage va CategoryPage trong schema.
 * Dung generic <T> nen dung duoc cho ca Product lan Category.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
	private List<T> content;
	private int totalPages;
	private long totalElements;
	private int currentPage;
}
