package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.Product_Attachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Product_Attachment, Long> {

}
