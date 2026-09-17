package vn.iotstar.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Product;
import vn.iotstar.repository.ProductRepository;

@Service
public class ProductServiceImpl implements IProductService {

	@Autowired
	private ProductRepository productRepository;

	/**
	 * Luu san pham.
	 * Khi CAP NHAT (productId != null) ma khong chon anh moi thi giu lai ten anh
	 * cu. Neu khong truyen createDate thi giu lai ngay tao cu.
	 */
	@Override
	public <S extends Product> S save(S entity) {
		if (entity.getProductId() != null) {
			Optional<Product> optOld = productRepository.findById(entity.getProductId());
			if (optOld.isPresent()) {
				Product old = optOld.get();
				if (entity.getImages() == null || entity.getImages().trim().isEmpty()) {
					entity.setImages(old.getImages());
				}
				if (entity.getCreateDate() == null) {
					entity.setCreateDate(old.getCreateDate());
				}
			}
		} else {
			if (entity.getCreateDate() == null) {
				entity.setCreateDate(new Date());
			}
		}
		return productRepository.save(entity);
	}

	@Override
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@Override
	public Page<Product> findAll(Pageable pageable) {
		return productRepository.findAll(pageable);
	}

	@Override
	public List<Product> findAll(Sort sort) {
		return productRepository.findAll(sort);
	}

	@Override
	public Optional<Product> findById(Long id) {
		return productRepository.findById(id);
	}

	@Override
	public List<Product> findAllById(Iterable<Long> ids) {
		return productRepository.findAllById(ids);
	}

	@Override
	public <S extends Product> Optional<S> findOne(Example<S> example) {
		return productRepository.findOne(example);
	}

	@Override
	public long count() {
		return productRepository.count();
	}

	@Override
	public void delete(Product entity) {
		productRepository.delete(entity);
	}

	@Override
	public void deleteById(Long id) {
		productRepository.deleteById(id);
	}

	@Override
	public List<Product> findByProductNameContaining(String productName) {
		return productRepository.findByProductNameContaining(productName);
	}

	@Override
	public Page<Product> findByProductNameContaining(String productName, Pageable pageable) {
		return productRepository.findByProductNameContaining(productName, pageable);
	}

	@Override
	public Optional<Product> findByProductName(String productName) {
		return productRepository.findByProductName(productName);
	}

	@Override
	public List<Product> findByCreateDate(Date createDate) {
		return productRepository.findByCreateDate(createDate);
	}

	@Override
	public List<Product> findByCategoryId(Long categoryId) {
		return productRepository.findByCategory_CategoryId(categoryId);
	}

	@Override
	public List<Product> findAllByOrderByUnitPriceAsc() {
		return productRepository.findAllByOrderByUnitPriceAsc();
	}
}
