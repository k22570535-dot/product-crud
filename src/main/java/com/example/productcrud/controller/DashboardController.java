package com.example.productcrud.controller;

import com.example.productcrud.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
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

        return "dashboard";
    }
}