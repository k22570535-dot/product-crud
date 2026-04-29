package com.example.productcrud.config;

import com.example.productcrud.model.Category;
import com.example.productcrud.repository.CategoryRepository;
import org.springframework.context.annotation.Lazy; // <-- Tambahkan import ini
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter implements Converter<String, Category> {

    private final CategoryRepository categoryRepository;

    // PERBAIKAN: Tambahkan @Lazy di dalam parameter constructor ini
    public CategoryConverter(@Lazy CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category convert(String id) {
        if (id == null || id.isEmpty() || id.equals("0")) {
            return null;
        }

        // Mengubah ID String dari form menjadi Long dan mencari objeknya di database
        try {
            Long categoryId = Long.valueOf(id);
            return categoryRepository.findById(categoryId).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}