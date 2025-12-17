package com.oojog.oojogtest.repositories;

import com.oojog.oojogtest.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    // placeholder method (need custom query)
    List<Category> findAllDescendants(@Param("prentID") String parentId);
}
