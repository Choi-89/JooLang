package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByCategory(String category);
}
