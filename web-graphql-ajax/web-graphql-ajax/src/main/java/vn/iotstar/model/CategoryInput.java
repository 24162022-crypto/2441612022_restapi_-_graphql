package vn.iotstar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input cho mutation createCategory / updateCategory.
 * icon la TEN FILE da upload truoc qua REST /api/upload.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryInput {
	private String categoryName;
	private String icon;
}
