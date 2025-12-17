package com.oojog.oojogtest.repositories;

import com.oojog.oojogtest.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
