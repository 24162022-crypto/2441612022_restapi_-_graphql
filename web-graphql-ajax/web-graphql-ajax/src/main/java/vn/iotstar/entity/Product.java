package vn.iotstar.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity Product - anh xa toi bang Products
 * GIU NGUYEN ten field theo tai lieu cua giang vien
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Products")
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;

	// columnDefinition nvarchar de luu duoc tieng Viet co dau tren SQL Server
	@Column(length = 500, columnDefinition = "nvarchar(500) not null")
	private String productName;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false)
	private double unitPrice;

	// Ten file anh luu trong thu muc uploads
	@Column(length = 200)
	private String images;

	@Column(columnDefinition = "nvarchar(500) not null")
	private String description;

	@Column(nullable = false)
	private double discount;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	// 1 = dang ban, 0 = ngung ban
	@Column(nullable = false)
	private short status;

	/**
	 * Quan he N-1 voi Category.
	 * @JsonIgnore theo dung tai lieu -> JSON tra ve cua Product KHONG co category.
	 * Vi vay controller map sang ProductModel de bo sung categoryId/categoryName.
	 */
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "categoryId")
	private Category category;
}
