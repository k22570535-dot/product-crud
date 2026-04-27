package com.example.productcrud.service;

import com.example.productcrud.dto.CategoryRequest;
import com.example.productcrud.model.Category;
import com.example.productcrud.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findByUsername(String username) {
        return categoryRepository.findByUsername(username);
    }

    public boolean existsByNameAndUsername(String name, String username) {
        return categoryRepository.existsByNameAndUsername(name, username);
    }

    @Transactional
    public void saveFromRequest(CategoryRequest request, String username) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        // Asumsi model Category memiliki field username untuk menyimpan pemilik data
        category.setUsername(username);
        categoryRepository.save(category);
    }

    public Optional<Category> findByIdAndUsername(Long id, String username) {
        return categoryRepository.findByIdAndUsername(id, username);
    }

    public boolean existsByNameAndUsernameExcludingId(String name, String username, Long id) {
        // Menggunakan method bawaan Spring Data JPA untuk mengecualikan ID tertentu
        return categoryRepository.existsByNameAndUsernameAndIdNot(name, username, id);
    }

    @Transactional
    public void save(Category category, String username) {
        category.setUsername(username);
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteByIdAndUsername(Long id, String username) {
        categoryRepository.deleteByIdAndUsername(id, username);
    }
}