package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Category;
import vn.iotstar.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements ICategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	/**
	 * Luu danh muc.
	 * Xu ly quan trong: khi CAP NHAT (categoryId != null) ma nguoi dung khong
	 * chon anh moi (icon rong) thi phai giu lai icon cu trong DB, neu khong
	 * anh cu se bi ghi de thanh null.
	 */
	@Override
	public <S extends Category> S save(S entity) {
		if (entity.getCategoryId() != null) {
			Optional<Category> optOld = categoryRepository.findById(entity.getCategoryId());
			if (optOld.isPresent()) {
				Category old = optOld.get();
				if (entity.getIcon() == null || entity.getIcon().trim().isEmpty()) {
					entity.setIcon(old.getIcon());
				}
			}
		}
		return categoryRepository.save(entity);
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public Page<Category> findAll(Pageable pageable) {
		return categoryRepository.findAll(pageable);
	}

	@Override
	public List<Category> findAll(Sort sort) {
		return categoryRepository.findAll(sort);
	}

	@Override
	public Optional<Category> findById(Long id) {
		return categoryRepository.findById(id);
	}

	@Override
	public List<Category> findAllById(Iterable<Long> ids) {
		return categoryRepository.findAllById(ids);
	}

	@Override
	public <S extends Category> Optional<S> findOne(Example<S> example) {
		return categoryRepository.findOne(example);
	}

	@Override
	public long count() {
		return categoryRepository.count();
	}

	@Override
	public void delete(Category entity) {
		categoryRepository.delete(entity);
	}

	@Override
	public void deleteById(Long id) {
		categoryRepository.deleteById(id);
	}

	@Override
	public List<Category> findByCategoryNameContaining(String categoryName) {
		return categoryRepository.findByCategoryNameContaining(categoryName);
	}

	@Override
	public Page<Category> findByCategoryNameContaining(String categoryName, Pageable pageable) {
		return categoryRepository.findByCategoryNameContaining(categoryName, pageable);
	}

	@Override
	public Optional<Category> findByCategoryName(String categoryName) {
		return categoryRepository.findByCategoryName(categoryName);
	}
}
