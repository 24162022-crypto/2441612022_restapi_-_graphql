package vn.iotstar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input cho mutation createProduct / updateProduct.
 * Ten field phai TRUNG voi ten trong input ProductInput cua schema.graphqls
 * thi Spring GraphQL moi tu map duoc.
 * Luu y: images la TEN FILE da upload truoc qua REST /api/upload, khong phai file.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInput {
	private String productName;
	private int quantity;
	private double unitPrice;
	private String images;
	private String description;
	private double discount;
	private short status;
	private Long categoryId;
}
