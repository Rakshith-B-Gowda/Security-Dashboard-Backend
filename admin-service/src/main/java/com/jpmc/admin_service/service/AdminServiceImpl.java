package com.jpmc.admin_service.service;

import com.jpmc.admin_service.model.Admin;
import com.jpmc.admin_service.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService{
    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public List<Admin> listPending() {
        // Assuming "pending" is a status in the Admin model
        return adminRepository.findByStatus("PENDING");
    }

    @Override
    public void approve(Long id) {
        Optional<Admin> adminOptional = adminRepository.findById(id);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            admin.setStatus("APPROVED");
            adminRepository.save(admin);
        } else {
            // Handle not found scenario, e.g., throw an exception
            throw new RuntimeException("Admin request with ID " + id + " not found.");
        }
    }

    @Override
    public void reject(Long id) {
        Optional<Admin> adminOptional = adminRepository.findById(id);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            admin.setStatus("REJECTED");
            adminRepository.save(admin);
        } else {
            // Handle not found scenario, e.g., throw an exception
            throw new RuntimeException("Admin request with ID " + id + " not found.");
        }
    }
}
