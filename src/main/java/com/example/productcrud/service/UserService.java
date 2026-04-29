package com.example.productcrud.service;

import com.example.productcrud.dto.EditProfileRequest;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- PERBAIKAN: Tambahkan method findByUsername ---
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // --- PERBAIKAN: Tambahkan method updateProfile ---
    @Transactional
    public void updateProfile(String username, EditProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan: " + username));

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setBio(request.getBio());
        user.setProfileImageUrl(request.getProfileImageUrl());

        userRepository.save(user);
    }

    @Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        // VALIDASI: Cek apakah password lama sesuai dengan yang ada di DB (BCrypt)
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false; // Password lama salah
        }

        // SIMPAN: Encode password baru sebelum disimpan
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}