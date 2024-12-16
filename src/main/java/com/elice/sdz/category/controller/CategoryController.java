package com.elice.sdz.category.controller;

import com.elice.sdz.category.dto.CategoryRequestDTO;
import com.elice.sdz.category.entity.Category;
import com.elice.sdz.category.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody @Valid CategoryRequestDTO categoryRequestDTO) {
        Category createdCategory = categoryService.createCategory(categoryRequestDTO);

        return ResponseEntity.ok(createdCategory);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable @NotNull Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);

        return ResponseEntity.ok(category);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable @NotNull Long categoryId, @RequestBody @Valid CategoryRequestDTO categoryRequestDTO) {
        Category updatedCategory = categoryService.updateCategory(categoryId, categoryRequestDTO);

        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @NotNull Long categoryId) {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }
}
