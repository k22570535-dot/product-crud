package com.example.productcrud.repository;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // PERBAIKAN: Tambahkan filter p.category.username = :username agar user tidak bisa melihat produk orang lain
    @Query("SELECT p FROM Product p WHERE p.category.username = :username " +
            "AND LOWER(p.name) LIKE LOWER(:keyword) " +
            "AND (:category IS NULL OR p.category = :category)")
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 @Param("category") Category category,
                                 @Param("username") String username,
                                 Pageable pageable);
}