package com.elice.sdz.category.service;

import com.elice.sdz.category.dto.CategoryRequestDTO;
import com.elice.sdz.category.dto.CategoryResponseDTO;
import com.elice.sdz.category.entity.Category;
import com.elice.sdz.category.repository.CategoryRepository;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final long CAPACITY = 5;
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        Category newCategory = categoryRequestDTO.toEntity();
        int categoryCount = categoryRepository.countByParentIdNull();

        if (categoryRepository.findByCategoryName(newCategory.getCategoryName()).isPresent()) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        if (categoryCount >= CAPACITY) {
            throw new CustomException(ErrorCode.CATEGORY_CAPACITY_EXCEEDED);
        }

        return categoryRepository.save(newCategory).toResponseDTO();
    }

    @Transactional
    public CategoryResponseDTO createChildCategory(CategoryRequestDTO categoryRequestDTO) {
        Category newCategory = categoryRequestDTO.toEntity();
        int childCategoryCount = categoryRepository.countByParentId(newCategory.getParentId());

        if (categoryRepository.findByCategoryName(newCategory.getCategoryName()).isPresent()) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        if (childCategoryCount >= CAPACITY) {
            throw new CustomException(ErrorCode.CATEGORY_CAPACITY_EXCEEDED);
        }

        return categoryRepository.save(newCategory).toResponseDTO();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(Category::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        return category.toResponseDTO();
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long categoryId, CategoryRequestDTO categoryRequestDTO) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if (categoryRepository.findByCategoryName(categoryRequestDTO.getCategoryName()).isPresent()) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        existingCategory.setCategoryName(categoryRequestDTO.getCategoryName());

        return categoryRepository.save(existingCategory).toResponseDTO();
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!category.getProducts().isEmpty()) {
            throw new CustomException(ErrorCode.CATEGORY_WITH_PRODUCTS);
        }

        if (!category.getSubCategories().isEmpty()) {
            throw new CustomException(ErrorCode.CATEGORY_WITH_PRODUCTS);
        }

        categoryRepository.deleteById(categoryId);
    }
}
