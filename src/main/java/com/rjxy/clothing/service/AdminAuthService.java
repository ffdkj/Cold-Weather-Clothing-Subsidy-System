package com.rjxy.clothing.service;

import com.rjxy.clothing.config.AppProperties;
import com.rjxy.clothing.model.AdminUser;
import com.rjxy.clothing.repo.AdminUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminAuthService {
    private final List<String> whitelist;
    private final AdminUserRepository adminRepo;
    public AdminAuthService(AppProperties props, AdminUserRepository adminRepo) {
        this.whitelist = props.getAdminWhitelist();
        this.adminRepo = adminRepo;
    }
    public boolean isAuthorized(String userId) {
        if (userId == null) return false;
        if (whitelist.contains(userId)) return true;
        Optional<AdminUser> user = adminRepo.findByUsername(userId);
        return user.isPresent();
    }
    public String roleOf(String userId) {
        if (userId == null) return null;
        if (whitelist.contains(userId)) return "counselor";
        return adminRepo.findByUsername(userId).map(AdminUser::getRole).orElse(null);
    }
    public boolean hasRole(String userId, String role) {
        String r = roleOf(userId);
        return r != null && role != null && r.equalsIgnoreCase(role);
    }
    public Optional<AdminUser> login(String username, String password) {
        if (username == null || password == null) return Optional.empty();
        Optional<AdminUser> u = adminRepo.findByUsername(username);
        if (u.isPresent() && password.equals(u.get().getPassword())) return u;
        return Optional.empty();
    }
}
