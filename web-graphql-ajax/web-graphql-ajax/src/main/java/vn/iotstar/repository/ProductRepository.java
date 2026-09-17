package vn.iotstar.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByProductNameContaining(String productName);

	Page<Product> findByProductNameContaining(String productName, Pageable pageable);

	Optional<Product> findByProductName(String productName);

	List<Product> findByCreateDate(Date createDate);

	/**
	 * Tim tat ca san pham thuoc 1 danh muc.
	 * Dau gach duoi giup Spring Data hieu ranh gioi: category -> categoryId
	 */
	List<Product> findByCategory_CategoryId(Long categoryId);

	// Lay tat ca san pham sap xep gia tang dan
	List<Product> findAllByOrderByUnitPriceAsc();
}
