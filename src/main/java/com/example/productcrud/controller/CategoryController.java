package com.example.productcrud.controller;

import com.example.productcrud.dto.CategoryRequest; // <-- Ini yang sebelumnya salah (CategoryController)
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

    // ─── LIST ────────────────────────────────────────────────────────────────

    @GetMapping
    public String listCategories(Authentication authentication, Model model) {
        List<Category> categories = categoryService.findByUsername(authentication.getName());
        model.addAttribute("categories", categories);
        return "category/list";
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryRequest", new CategoryRequest());
        return "category/form";
    }

    @PostMapping("/save")
    public String saveCategory(
            @Valid @ModelAttribute("categoryRequest") CategoryRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();

        // Kembalikan ke form jika ada error validasi Bean
        if (bindingResult.hasErrors()) {
            return "category/form";
        }

        // Cek duplikasi nama
        if (categoryService.existsByNameAndUsername(request.getName().trim(), username)) {
            bindingResult.rejectValue("name", "duplicate", "Nama kategori sudah ada");
            return "category/form";
        }

        categoryService.saveFromRequest(request, username);
        redirectAttributes.addFlashAttribute("success", "Kategori berhasil ditambahkan!");
        return "redirect:/categories";
    }

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

    @PostMapping("/{id}/update")
    public String updateCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute("categoryRequest") CategoryRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();

        // Kembalikan ke form jika ada error validasi Bean
        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryId", id);
            return "category/form";
        }

        // Pastikan kategori milik user ini
        Category existing = categoryService.findByIdAndUsername(id, username)
                .orElse(null);
        if (existing == null) {
            redirectAttributes.addFlashAttribute("error", "Kategori tidak ditemukan atau bukan milik Anda");
            return "redirect:/categories";
        }

        // Cek duplikasi nama, kecuali milik dirinya sendiri
        if (categoryService.existsByNameAndUsernameExcludingId(request.getName().trim(), username, id)) {
            bindingResult.rejectValue("name", "duplicate", "Nama kategori sudah ada");
            model.addAttribute("categoryId", id);
            return "category/form";
        }

        existing.setName(request.getName().trim());
        existing.setDescription(request.getDescription());
        categoryService.save(existing, username);
        redirectAttributes.addFlashAttribute("success", "Kategori berhasil diperbarui!");
        return "redirect:/categories";
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String deleteCategory(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();

        // Verifikasi ownership sebelum hapus
        boolean exists = categoryService.findByIdAndUsername(id, username).isPresent();
        if (!exists) {
            redirectAttributes.addFlashAttribute("error", "Kategori tidak ditemukan atau bukan milik Anda");
            return "redirect:/categories";
        }

        categoryService.deleteByIdAndUsername(id, username);
        redirectAttributes.addFlashAttribute("success", "Kategori berhasil dihapus!");
        return "redirect:/categories";
    }
}