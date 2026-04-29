package com.example.productcrud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {

    @NotBlank(message = "Nama kategori tidak boleh kosong")
    @Size(max = 100, message = "Nama kategori maksimal 100 karakter")
    private String name;

    private String description;

    public CategoryRequest() {
    }

    public String getName() {
        return name;
    }

    // PERBAIKAN: Gunakan String, bukan Class
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    // PERBAIKAN: Gunakan String, bukan Class
    public void setDescription(String description) {
        this.description = description;
    }
}