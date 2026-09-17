package vn.iotstar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	// Tim kiem gan dung theo ten danh muc (khong phan trang)
	List<Category> findByCategoryNameContaining(String categoryName);

	// Tim kiem gan dung theo ten danh muc (co phan trang)
	Page<Category> findByCategoryNameContaining(String categoryName, Pageable pageable);

	// Tim chinh xac theo ten danh muc
	Optional<Category> findByCategoryName(String categoryName);
}
