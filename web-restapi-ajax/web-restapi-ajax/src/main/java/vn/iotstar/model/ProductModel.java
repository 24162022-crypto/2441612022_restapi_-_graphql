package vn.iotstar.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO tra ve cho client.
 * LY DO CAN LOP NAY: entity Product co @JsonIgnore tren field category nen khi
 * serialize JSON se mat thong tin danh muc. Lop nay bo sung categoryId va
 * categoryName de giao dien hien thi duoc ten danh muc, dong thoi tranh loi
 * StackOverflowError do quan he hai chieu.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
	private Long productId;
	private String productName;
	private int quantity;
	private double unitPrice;
	private String images;
	private String description;
	private double discount;
	private Date createDate;
	private short status;
	private Long categoryId;
	private String categoryName;
}
