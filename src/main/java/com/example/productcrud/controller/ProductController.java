package com.example.productcrud.controller;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.service.ProductService;
import com.example.productcrud.repository.CategoryRepository;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "category", required = false) String categoryStr,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Authentication authentication,
            Model model) {

        String username = authentication.getName(); // Ambil username yang sedang login
        Category category = null;

        if (categoryStr != null && !categoryStr.trim().isEmpty()) {
            try {
                Long categoryId = Long.parseLong(categoryStr.trim());
                // Pastikan user tidak meretas value dropdown dengan ID kategori orang lain
                category = categoryRepository.findByIdAndUsername(categoryId, username).orElse(null);
            } catch (NumberFormatException e) {
                category = null;
            }
        }

        int pageSize = 10;
        // Memasukkan variabel username ke dalam service
        Page<Product> productPage = productService.searchProducts(keyword, category, username, page, pageSize);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", categoryStr != null ? categoryStr : "");

        model.addAttribute("categories", categoryRepository.findByUsername(username));

        return "product/list";
    }

    @GetMapping("/products/new")
    public String showCreateForm(Model model, Authentication authentication) {
        Product product = new Product();
        model.addAttribute("product", product);

        // Ambil kategori milik user login
        model.addAttribute("categories", categoryRepository.findByUsername(authentication.getName()));
        return "product/form";
    }

    @GetMapping("/products/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        return productService.findById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    // Ambil kategori milik user login
                    model.addAttribute("categories", categoryRepository.findByUsername(authentication.getName()));
                    return "product/form";
                })
                .orElse("redirect:/products");
    }

    // PERBAIKAN: Method saveProduct diubah agar dapat menangkap error dan tidak membuat halaman putih
    @PostMapping("/products/save")
    public String saveProduct(
            @ModelAttribute Product product,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 1. Tangkap error jika ada tipe data yang tidak cocok dari form
        if (bindingResult.hasErrors()) {
            System.out.println("Terdapat Error pada Form: " + bindingResult.getAllErrors());
            // Kembalikan ke form beserta daftar kategori agar dropdown tidak kosong
            model.addAttribute("categories", categoryRepository.findByUsername(authentication.getName()));
            return "product/form";
        }

        // 2. Set tanggal dibuat secara otomatis jika ini produk baru (ID masih kosong)
        if (product.getId() == null) {
            product.setCreatedAt(LocalDate.now());
        }

        try {
            productService.save(product);
            redirectAttributes.addFlashAttribute("successMessage", "Produk berhasil disimpan!");
        } catch (Exception e) {
            System.out.println("Error saat menyimpan ke database: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menyimpan produk: " + e.getMessage());
        }

        return "redirect:/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Produk berhasil dihapus!");
        return "redirect:/products";
    }
}