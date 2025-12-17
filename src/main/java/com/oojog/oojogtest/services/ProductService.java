package com.oojog.oojogtest.services;

import com.oojog.oojogtest.dtos.CreateProductRequest;
import com.oojog.oojogtest.dtos.ProductDto;
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


    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantityInStock()
        );
    }

}
