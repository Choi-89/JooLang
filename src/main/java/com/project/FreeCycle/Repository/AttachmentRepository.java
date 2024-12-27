package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.Product_Attachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Product_Attachment, Long> {

    List<Product_Attachment> findAllByProduct_Id(Long product_id);
    List<Product_Attachment> findByOriginFilename(String originalName);
}
