package com.oojog.oojogtest.services;

import com.oojog.oojogtest.dtos.CategoryDto;
import com.oojog.oojogtest.dtos.CreateCategoryRequest;
import com.oojog.oojogtest.entities.Category;
import com.oojog.oojogtest.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;

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

        return new CategoryDto(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getParent() != null ? saved.getParent().getId() : null,
                new LinkedList<>() // new category no children by default
        );

    }


    private Category findByIdOrThrow(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

}
