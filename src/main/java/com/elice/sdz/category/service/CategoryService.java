package com.elice.sdz.category.service;

import com.elice.sdz.category.dto.CategoryRequestDTO;
import com.elice.sdz.category.entity.Category;
import com.elice.sdz.category.repository.CategoryRepository;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(CategoryRequestDTO categoryRequestDTO) {
        Category newCategory = categoryRequestDTO.toEntity();

        if (categoryRepository.findByCategoryName(newCategory.getCategoryName()).isPresent()) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        return categoryRepository.save(newCategory);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    public Category updateCategory(Long categoryId, CategoryRequestDTO categoryRequestDTO) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if (categoryRepository.findByCategoryName(categoryRequestDTO.getCategoryName()).isPresent()) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        existingCategory.setCategoryName(categoryRequestDTO.getCategoryName());

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryRepository.deleteById(categoryId);
    }
}
