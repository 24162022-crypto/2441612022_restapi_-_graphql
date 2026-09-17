package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import vn.iotstar.entity.Category;

public interface ICategoryService {

	<S extends Category> S save(S entity);

	List<Category> findAll();

	Page<Category> findAll(Pageable pageable);

	List<Category> findAll(Sort sort);

	Optional<Category> findById(Long id);

	List<Category> findAllById(Iterable<Long> ids);

	<S extends Category> Optional<S> findOne(Example<S> example);

	long count();

	void delete(Category entity);

	void deleteById(Long id);

	List<Category> findByCategoryNameContaining(String categoryName);

	Page<Category> findByCategoryNameContaining(String categoryName, Pageable pageable);

	Optional<Category> findByCategoryName(String categoryName);
}
