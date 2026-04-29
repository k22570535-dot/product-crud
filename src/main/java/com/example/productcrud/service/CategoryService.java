package com.example.productcrud.service;

import com.example.productcrud.dto.CategoryRequest;
import com.example.productcrud.model.Category;
import com.example.productcrud.repository.CategoryRepository;
import jakarta.validation.Valid;
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

    // ─── QUERY LOGIC ────────────────────────────────────────────────────────

    /**
     * Mengambil semua kategori milik user beserta daftar produknya.
     * Kita menggunakan method repository yang sudah di-custom agar tidak terjadi N+1 query.
     */
    public List<Category> findByUsername(String username) {
        // Menggunakan filter kosong agar memanggil query JOIN FETCH produk
        return categoryRepository.searchByUsernameAndNameWithProducts(username, "");
    }

    /**
     * Mencari kategori berdasarkan keyword dan menampilkan produk terkait.
     */
    public List<Category> findByUsernameAndKeyword(String username, String keyword) {
        String searchKeyword = (keyword != null) ? keyword.trim() : "";
        // Selalu gunakan method WithProducts agar daftar produk muncul di UI
        return categoryRepository.searchByUsernameAndNameWithProducts(username, searchKeyword);
    }

    public Optional<Category> findByIdAndUsername(Long id, String username) {
        return categoryRepository.findByIdAndUsername(id, username);
    }

    // ─── VALIDATION LOGIC ────────────────────────────────────────────────────

    public boolean existsByNameAndUsername(String name, String username) {
        return categoryRepository.existsByNameAndUsername(name, username);
    }

    public boolean existsByNameAndUsernameExcludingId(String name, String username, Long id) {
        return categoryRepository.existsByNameAndUsernameAndIdNot(name, username, id);
    }


    // ─── WRITE LOGIC ─────────────────────────────────────────────────────────

    @Transactional
    public void saveFromRequest(CategoryRequest request, String username) {
        Category category = new Category();
        // PERBAIKAN: Gunakan setName dan setDescription
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setUsername(username);
        categoryRepository.save(category);
    }

// ... (kode selanjutnya tetap sama)

    @Transactional
    public void updateFromRequest(Long id, @Valid CategoryRequest request, String username) {
        Category category = categoryRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan atau akses ditolak"));

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        // Hibernate dirty checking akan mengupdate database otomatis di akhir transaksi
    }

    @Transactional
    public void save(Category category, String username) {
        category.setUsername(username);
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteByIdAndUsername(Long id, String username) {
        if (!categoryRepository.existsByIdAndUsername(id, username)) {
            throw new RuntimeException("Kategori tidak ditemukan atau akses ditolak");
        }
        categoryRepository.deleteByIdAndUsername(id, username);
    }
}