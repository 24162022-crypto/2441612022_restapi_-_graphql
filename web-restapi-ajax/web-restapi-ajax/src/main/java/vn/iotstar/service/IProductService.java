package vn.iotstar.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import vn.iotstar.entity.Product;

public interface IProductService {

	<S extends Product> S save(S entity);

	List<Product> findAll();

	Page<Product> findAll(Pageable pageable);

	List<Product> findAll(Sort sort);

	Optional<Product> findById(Long id);

	List<Product> findAllById(Iterable<Long> ids);

	<S extends Product> Optional<S> findOne(Example<S> example);

	long count();

	void delete(Product entity);

	void deleteById(Long id);

	List<Product> findByProductNameContaining(String productName);

	Page<Product> findByProductNameContaining(String productName, Pageable pageable);

	Optional<Product> findByProductName(String productName);

	List<Product> findByCreateDate(Date createDate);

	List<Product> findByCategoryId(Long categoryId);

	List<Product> findAllByOrderByUnitPriceAsc();
}
