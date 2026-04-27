package com.example.productcrud.repository;

import com.example.productcrud.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUsername(String username);

    Optional<Category> findByIdAndUsername(Long id, String username);

    boolean existsByNameAndUsername(String name, String username);

    // Method untuk mengecek nama yang sama, tetapi abaikan ID yang sedang di-edit
    boolean existsByNameAndUsernameAndIdNot(String name, String username, Long id);

    void deleteByIdAndUsername(Long id, String username);
}