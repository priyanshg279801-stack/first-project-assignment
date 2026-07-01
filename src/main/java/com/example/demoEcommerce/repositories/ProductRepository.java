package com.example.demoEcommerce.repositories;




import com.example.demoEcommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    List<Product> findByProductNameContainingIgnoreCase(String productName);

    boolean existsByProductName(String productName);
}
