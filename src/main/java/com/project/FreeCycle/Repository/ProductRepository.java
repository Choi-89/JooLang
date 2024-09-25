package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findById(Long integer);
    Product findByName(String name);

}