package com.example.productcrud.repository;

import com.example.productcrud.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 1. Cari semua kategori milik user tertentu
    List<Category> findByUsername(String username);

    // 2. Filter: Cari berdasarkan keyword (Case-Insensitive) dan Username
    @Query("SELECT c FROM Category c WHERE c.username = :username " +
            "AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Category> searchByUsernameAndName(@Param("username") String username,
                                           @Param("keyword") String keyword);

    // 3. Cari satu kategori spesifik milik user (Security Check)
    Optional<Category> findByIdAndUsername(Long id, String username);

    // 4. Cek apakah nama sudah digunakan oleh user yang sama (untuk Create)
    boolean existsByNameAndUsername(String name, String username);

    // 5. Cek apakah nama sudah digunakan user lain, tapi abaikan ID milik sendiri (untuk Update)
    boolean existsByNameAndUsernameAndIdNot(String name, String username, Long id);

    // 6. Cek keberadaan data sebelum dihapus
    boolean existsByIdAndUsername(Long id, String username);

    // 7. Hapus kategori milik user tertentu
    @Modifying
    @Query("DELETE FROM Category c WHERE c.id = :id AND c.username = :username")
    void deleteByIdAndUsername(@Param("id") Long id, @Param("username") String username);

    // 8. PERBAIKAN: Menambahkan @Query untuk mengambil Kategori beserta Produknya
    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products " +
            "WHERE c.username = :username " +
            "AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Category> searchByUsernameAndNameWithProducts(@Param("username") String username,
                                                       @Param("keyword") String keyword);

}