package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.Product_Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureRepository extends JpaRepository<Product_Picture, Long> {

}
