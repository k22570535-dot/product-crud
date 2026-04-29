package com.example.productcrud.controller;

import com.example.productcrud.dto.CategoryRequest;
import com.example.productcrud.model.Category;
import com.example.productcrud.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ─── LIST & FILTER ───────────────────────────────────────────────────────

    @GetMapping
    public String listCategories(
            @RequestParam(name = "keyword", required = false) String keyword,
            Authentication authentication,
            Model model) {

        String username = authentication.getName();

        // Menggunakan service yang sudah mendukung pencarian keyword + JOIN FETCH produk
        List<Category> categories = categoryService.findByUsernameAndKeyword(username, keyword);

        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword); // Dikembalikan ke view untuk mengisi input pencarian
        return "category/list";
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("categoryRequest")) {
            model.addAttribute("categoryRequest", new CategoryRequest());
        }
        return "category/form";
    }

    @PostMapping("/save")
    public String saveCategory(
            @Valid @ModelAttribute("categoryRequest") CategoryRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();

        if (bindingResult.hasErrors()) {
            return "category/form";
        }

        // Validasi duplikasi nama (case-insensitive sebaiknya dilakukan di service)
        if (categoryService.existsByNameAndUsername(request.getName().trim(), username)) {
            bindingResult.rejectValue("name", "duplicate", "Nama kategori sudah ada");
            return "category/form";
        }

        categoryService.saveFromRequest(request, username);
        redirectAttributes.addFlashAttribute("success", "Kategori berhasil ditambahkan!");
        return "redirect:/categories";
    }

    // ... (import dan kode awal tetap sama)

    // ─── EDIT ────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        return categoryService.findByIdAndUsername(id, authentication.getName())
                .map(category -> {
                    CategoryRequest request = new CategoryRequest();
                    // PERBAIKAN: Ambil nilai String, bukan memanggil getClass()
                    request.setName(category.getName());
                    request.setDescription(category.getDescription());

                    model.addAttribute("categoryRequest", request);
                    model.addAttribute("categoryId", id);
                    return "category/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Kategori tidak ditemukan");
                    return "redirect:/categories";
                });
    }

// ... (kode selanjutnya tetap sama)

    @PostMapping("/{id}/update")
    public String updateCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute("categoryRequest") CategoryRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();

        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryId", id);
            return "category/form";
        }

        // Validasi duplikasi nama kecuali ID ini sendiri
        if (categoryService.existsByNameAndUsernameExcludingId(request.getName().trim(), username, id)) {
            bindingResult.rejectValue("name", "duplicate", "Nama kategori sudah digunakan");
            model.addAttribute("categoryId", id);
            return "category/form";
        }

        try {
            categoryService.updateFromRequest(id, request, username);
            redirectAttributes.addFlashAttribute("success", "Kategori berhasil diperbarui!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/categories";
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String deleteCategory(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            categoryService.deleteByIdAndUsername(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Kategori dan produk di dalamnya berhasil dihapus!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus: " + e.getMessage());
        }

        return "redirect:/categories";
    }
}