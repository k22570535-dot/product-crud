package com.example.productcrud.controller;

<<<<<<< HEAD
import com.example.productcrud.service.DashboardService;
import org.springframework.security.core.Authentication;
=======
>>>>>>> 18f995cc5b79265810f700aa8032047163485a04
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
<<<<<<< HEAD
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();

        long totalProduk = dashboardService.getTotalProduk(username);

        model.addAttribute("totalProduk", totalProduk);
        model.addAttribute("totalNilaiInventory", dashboardService.getTotalNilaiInventory(username));
        model.addAttribute("totalAktif", dashboardService.getTotalProdukAktif(username));
        model.addAttribute("totalTidakAktif", dashboardService.getTotalProdukTidakAktif(username));
        model.addAttribute("produkPerKategori", dashboardService.getProdukPerKategori(username));
        model.addAttribute("lowStockList", dashboardService.getLowStockProducts(username));
        model.addAttribute("adaProduk", totalProduk > 0);
=======
    public String viewDashboard(Model model) {

        // --- Data Dummy (Contoh) untuk ditampilkan di Halaman ---
        // Nanti ini bisa diganti dengan data dari database
        String username = "Tim Product";
        int totalProduct = 150;
        int totalCategories = 10;
        // ----------------------------------------------------
>>>>>>> 18f995cc5b79265810f700aa8032047163485a04

        // Kirim data ke HTML menggunakan Model
        model.addAttribute("username", username);
        model.addAttribute("totalProduct", totalProduct);
        model.addAttribute("totalCategories", totalCategories);

        // Ini akan mencari file bernama dashboard.html di folder src/main/resources/templates
        return "dashboard";
    }
}