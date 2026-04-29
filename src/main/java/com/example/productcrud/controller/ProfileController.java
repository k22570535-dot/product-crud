package com.example.productcrud.controller;

import com.example.productcrud.dto.ChangePasswordRequest;
import com.example.productcrud.dto.EditProfileRequest;
import com.example.productcrud.model.User;
import com.example.productcrud.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(final UserService userService) {
        this.userService = userService;
    }

    // GET /profile -> Tampilkan halaman view profil user
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));
        model.addAttribute("user", user);
        return "profile";
    }

    // GET /profile/edit -> Tampilkan form edit profil
    @GetMapping("/profile/edit")
    public String showEditProfileForm(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        EditProfileRequest editProfileRequest = new EditProfileRequest();
        editProfileRequest.setFullName(user.getFullName());
        editProfileRequest.setEmail(user.getEmail());
        editProfileRequest.setPhoneNumber(user.getPhoneNumber());
        editProfileRequest.setAddress(user.getAddress());
        editProfileRequest.setBio(user.getBio());
        editProfileRequest.setProfileImageUrl(user.getProfileImageUrl());

        model.addAttribute("editProfileRequest", editProfileRequest);
        model.addAttribute("username", user.getUsername());
        return "edit-profile";
    }

    // POST /profile/edit -> Proses update profil
    @PostMapping("/profile/edit")
    public String processEditProfile(
            @ModelAttribute EditProfileRequest editProfileRequest,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        userService.updateProfile(authentication.getName(), editProfileRequest);
        redirectAttributes.addFlashAttribute("success", "Profil berhasil diperbarui!");
        return "redirect:/profile";
    }

    // GET /profile/change-password -> Tampilkan form ganti password
    @GetMapping("/profile/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "change-password";
    }

    // POST /profile/change-password -> Proses ganti password
    @PostMapping("/profile/change-password")
    public String processChangePassword(
            @ModelAttribute ChangePasswordRequest request,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();

        if (request.getOldPassword().isBlank() || request.getNewPassword().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Semua field harus diisi");
            return "redirect:/profile/change-password";
        }

        // 2. Validasi Konfirmasi Password Baru
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            redirectAttributes.addFlashAttribute("error", "Konfirmasi password baru tidak cocok");
            return "redirect:/profile/change-password";
        }

        // 3. Validasi Panjang Password (Contoh minimal 8 karakter)
        if (request.getNewPassword().length() < 8) {
            redirectAttributes.addFlashAttribute("error", "Password baru minimal 8 karakter");
            return "redirect:/profile/change-password";
        }

        boolean success = userService.changePassword(username, request.getOldPassword(), request.getNewPassword());

        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Password lama yang Anda masukkan salah");
            return "redirect:/profile/change-password";
        }

        redirectAttributes.addFlashAttribute("success", "Password berhasil diperbarui!");
        return "redirect:/profile";
    }
}