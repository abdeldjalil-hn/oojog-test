package com.oojog.oojogtest.services;

import com.oojog.oojogtest.dtos.CreateProductRequest;
import com.oojog.oojogtest.dtos.LinkProductToCategoryRequest;
import com.oojog.oojogtest.dtos.ProductDto;
import com.oojog.oojogtest.dtos.UpdateProductRequest;
import com.oojog.oojogtest.entities.Category;
import com.oojog.oojogtest.entities.Product;
import com.oojog.oojogtest.repositories.CategoryRepository;
import com.oojog.oojogtest.repositories.ProductRepository;

public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductDto create(CreateProductRequest createProductRequest) {

        Product product = new Product();
        product.setName(createProductRequest.getName());
        product.setDescription(createProductRequest.getDescription());
        product.setPrice(createProductRequest.getPrice());
        product.setQuantityInStock(createProductRequest.getQuantity() != 0 ? createProductRequest.getQuantity() : 0);

        if (createProductRequest.getCategoryId() != null && !createProductRequest.getCategoryId().isBlank()) {
            Category category = categoryRepository.findById(createProductRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        Product saved = productRepository.save(product);
        return toDto(saved);
    }

    public ProductDto update(String id, UpdateProductRequest updateProductRequest) {
        Product product = findByIdOrThrow(id);

        if (updateProductRequest.getName() != null && !updateProductRequest.getName().isBlank()) {
            product.setName(updateProductRequest.getName());
        }

        if (updateProductRequest.getDescription() != null && !updateProductRequest.getDescription().isBlank()) {
            product.setDescription(updateProductRequest.getDescription());
        }

        if (updateProductRequest.getPrice() != null) {
            product.setPrice(updateProductRequest.getPrice());
        }

        if (updateProductRequest.getQuantity() != null) {
            product.setQuantityInStock(updateProductRequest.getQuantity());
        }

        if (updateProductRequest.getCategoryId() != null) {
            if (updateProductRequest.getCategoryId().isBlank()) {
                product.setCategory(null);
            } else {
                Category category = categoryRepository
                        .findById(updateProductRequest.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));

                product.setCategory(category);
            }
        }

        Product saved = productRepository.save(product);
        return toDto(saved);
    }

    public void delete(String id) {
        Product product = findByIdOrThrow(id);
        productRepository.delete(product);
    }

    public ProductDto linkProductToCategory(LinkProductToCategoryRequest linkProductToCategoryRequest) {
        Product product = findByIdOrThrow(linkProductToCategoryRequest.getProductId());
        Category category = categoryRepository
                .findById(linkProductToCategoryRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setCategory(category);

        Product saved = productRepository.save(product);
        return toDto(saved);
    }


    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantityInStock()
        );
    }

    private Product findByIdOrThrow(String id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

}
