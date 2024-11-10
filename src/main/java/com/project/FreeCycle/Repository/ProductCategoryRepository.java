package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.ProductCategory;
import com.project.FreeCycle.Domain.ProductCategoryId;
import com.project.FreeCycle.Domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategoryId> {
    List<ProductCategory> findAllByCategory(Category category);
    ProductCategory findByProduct_Id(Long id);
}
