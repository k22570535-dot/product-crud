package com.example.productcrud.service;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    // PERBAIKAN: Menambahkan parameter "String username" agar cocok dengan Controller
    public Page<Product> searchProducts(String keyword, Category category, String username, int page, int size) {
        String kw = (keyword != null && !keyword.trim().isEmpty())
                ? "%" + keyword.trim() + "%"
                : "%%";

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        // Meneruskan username ke Repository
        return productRepository.searchProducts(kw, category, username, pageable);
    }
}