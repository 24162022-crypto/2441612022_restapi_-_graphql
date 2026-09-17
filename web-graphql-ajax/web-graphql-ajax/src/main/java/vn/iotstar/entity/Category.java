package vn.iotstar.entity;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity Category - anh xa toi bang Categories
 * GIU NGUYEN ten field theo tai lieu cua giang vien
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Categories")
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long categoryId;

	private String categoryName;

	// Ten file icon luu trong thu muc uploads (khong luu duong dan day du)
	private String icon;

	/**
	 * Quan he 1-N voi Product.
	 * @JsonIgnore de tranh StackOverflowError khi Jackson serialize vong lap
	 * Category -> products -> category -> products -> ...
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	private Set<Product> products;
}
