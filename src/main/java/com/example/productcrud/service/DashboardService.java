package com.example.productcrud.service;

import com.example.productcrud.model.Product;
import com.example.productcrud.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ProductRepository productRepository;

    public DashboardService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Gunakan findAll() lalu filter berdasarkan username kategori atau buat query baru di Repository
    // Di sini saya contohkan menggunakan filter stream agar cepat
    private List<Product> getMyProducts(String username) {
        return productRepository.findAll().stream()
                .filter(p -> p.getCategory() != null && username.equals(p.getCategory().getUsername()))
                .collect(Collectors.toList());
    }

    public long getTotalProduk(String username) {
        return getMyProducts(username).size();
    }

    public double getTotalNilaiInventory(String username) {
        return getMyProducts(username).stream()
                .mapToDouble(p -> p.getPrice() * p.getStock())
                .sum();
    }

    public long getTotalProdukAktif(String username) {
        return getMyProducts(username).stream()
                .filter(Product::isActive)
                .count();
    }

    public long getTotalProdukTidakAktif(String username) {
        return getMyProducts(username).stream()
                .filter(p -> !p.isActive())
                .count();
    }

    public Map<String, Long> getProdukPerKategori(String username) {
        return getMyProducts(username).stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory().getName(),
                        Collectors.counting()
                ));
    }

    public List<Product> getLowStockProducts(String username) {
        return getMyProducts(username).stream()
                .filter(p -> p.getStock() < 5)
                .collect(Collectors.toList());
    }
}