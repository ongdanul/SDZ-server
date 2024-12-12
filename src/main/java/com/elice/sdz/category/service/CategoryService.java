package com.elice.sdz.category.service;

import com.elice.sdz.category.dto.CategoryRequestDTO;
import com.elice.sdz.category.entity.Category;
import com.elice.sdz.category.repository.CategoryRepository;
import com.elice.sdz.global.exception.category.CategoryAlreadyExistsException;
import com.elice.sdz.global.exception.category.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category) {

        if (categoryRepository.findByCategoryName(category.getCategoryName()).isPresent()) {
            throw new CategoryAlreadyExistsException("카테고리 이름이 이미 존재합니다 : " + category.getCategoryName());
        }

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("카테고리가 존재하지 않습니다 : " + categoryId));
    }

    public Category updateCategory(Long categoryId, Category category) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("카테고리가 존재하지 않습니다 : " + categoryId));

        if (categoryRepository.findByCategoryName(category.getCategoryName()).isPresent()) {
            throw new CategoryAlreadyExistsException("카테고리 이름이 이미 존재합니다 : " + category.getCategoryName());
        }

        existingCategory.setCategoryName(category.getCategoryName());

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("카테고리가 존재하지 않습니다 : " + categoryId));

        categoryRepository.deleteById(categoryId);
    }
}
