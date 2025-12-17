package com.oojog.oojogtest.services;

import com.oojog.oojogtest.dtos.CategoryDto;
import com.oojog.oojogtest.dtos.CreateCategoryRequest;
import com.oojog.oojogtest.dtos.UpdateCategoryRequest;
import com.oojog.oojogtest.entities.Category;
import com.oojog.oojogtest.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDto create(CreateCategoryRequest createCategoryRequest) {

        Category category = new Category();
        category.setName(createCategoryRequest.getName());
        category.setDescription(createCategoryRequest.getDescription());

        if (createCategoryRequest.getParentId() != null && !createCategoryRequest.getParentId().isBlank()) {
            Category parent = findByIdOrThrow(createCategoryRequest.getParentId());
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);

        return toDto(saved, false); // new category no children by default

    }

    public CategoryDto update(String id, UpdateCategoryRequest updateCategoryRequest) {
        Category category = findByIdOrThrow(id);

        if (updateCategoryRequest.getName() != null && !updateCategoryRequest.getName().isBlank()) {
            category.setName(updateCategoryRequest.getName());
        }

        if (updateCategoryRequest.getDescription() != null && !updateCategoryRequest.getDescription().isBlank()) {
            category.setDescription(updateCategoryRequest.getDescription());
        }

        if (updateCategoryRequest.getParentId() != null) {
            if (updateCategoryRequest.getParentId().isBlank()) {
                // make the category a root category
                // can be extract to it's own method (makeCategoryRoot)
                category.setParent(null);
            } else {
                if (category.getId().equals(updateCategoryRequest.getParentId())) {
                    throw new RuntimeException("Circular reference");
                }

                Set<String> descendantIds = categoryRepository.findAllDescendants(category.getId())
                        .stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet());

                if (descendantIds.contains(updateCategoryRequest.getParentId())) {
                    throw new RuntimeException("Circular reference");
                }

                Category newParent = findByIdOrThrow(updateCategoryRequest.getParentId());
                category.setParent(newParent);
            }
        }

        Category saved = categoryRepository.save(category);
        return toDto(saved, true);
    }

    public void delete(String id) {
        Category category = findByIdOrThrow(id);

        if (category.hasChildren()) {
            throw new RuntimeException("Can not delete a category with children: " + id);
        }

        if (category.hasProducts()) {
            throw new RuntimeException("Can not delete a category with products: " + id);
        }

        categoryRepository.delete(category);
    }

    public void forceDelete(String id) {
        Category category = findByIdOrThrow(id);

        // JPA cascade configuration will handle all children dependencies are deleted
        categoryRepository.delete(category);
    }

    private Category findByIdOrThrow(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    private CategoryDto toDto(Category category, boolean withChildren) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getParent() != null ? category.getParent().getId() : null,
                withChildren ? category.getChildren().stream().map(c -> toDto(c, true)).toList() : List.of()
        );
    }

}
