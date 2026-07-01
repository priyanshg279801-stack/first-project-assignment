package com.example.demoEcommerce.services;


import com.example.demoEcommerce.entities.Product;
import com.example.demoEcommerce.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    // Create Product
    public Product createProduct(Product product) {

        if (productRepository.existsByProductName(product.getProductName())) {
            throw new IllegalArgumentException("Product already exists with name: " + product.getProductName());
        }

        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    // Get All Products
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get Product By ID
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found with id: " + id));
    }

    // Update Product
    public Product updateProduct(Long id, Product updatedProduct) {

        Product existingProduct = getProductById(id);

        existingProduct.setProductName(updatedProduct.getProductName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(existingProduct);
    }

    // Delete Product
    public void deleteProduct(Long id) {

        Product product = getProductById(id);

        productRepository.delete(product);
    }

    // Search by Category
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {

        return productRepository.findByCategory(category);
    }

    // Search by Product Name
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {

        return productRepository.findByProductNameContainingIgnoreCase(keyword);
    }

    // Increase Stock
    public Product increaseStock(Long productId, Integer quantity) {

        Product product = getProductById(productId);

        product.setStockQuantity(product.getStockQuantity() + quantity);

        return productRepository.save(product);
    }

    // Decrease Stock
    public Product decreaseStock(Long productId, Integer quantity) {

        Product product = getProductById(productId);

        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product: " + product.getProductName());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);

        return productRepository.save(product);
    }
}