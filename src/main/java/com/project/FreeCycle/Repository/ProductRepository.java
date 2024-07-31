package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
