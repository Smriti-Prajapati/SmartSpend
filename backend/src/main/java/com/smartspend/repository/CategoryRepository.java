package com.smartspend.repository;

import com.smartspend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(Category.CategoryType type);
    List<Category> findByTypeIn(List<Category.CategoryType> types);
}
