package com.example.productcrud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String viewDashboard(Model model) {

        // --- Data Dummy (Contoh) untuk ditampilkan di Halaman ---
        // Nanti ini bisa diganti dengan data dari database
        String username = "Tim Product";
        int totalProduct = 150;
        int totalCategories = 10;
        // ----------------------------------------------------

        // Kirim data ke HTML menggunakan Model
        model.addAttribute("username", username);
        model.addAttribute("totalProduct", totalProduct);
        model.addAttribute("totalCategories", totalCategories);

        // Ini akan mencari file bernama dashboard.html di folder src/main/resources/templates
        return "dashboard";
    }
}