package com.jpmc.admin_service.service;

import com.jpmc.admin_service.dto.AddRequestDto;
import com.jpmc.admin_service.mapper.ToAdminMapper;
import com.jpmc.admin_service.model.Admin;
import com.jpmc.admin_service.enums.Status; // Import the Status enum
import com.jpmc.admin_service.repository.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // For transactional operations

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public List<Admin> listPendingRequests() {
        // Use Status.PENDING enum value for lookup
        return adminRepository.findByStatus(Status.PENDING);
    }

    @Override
    @Transactional // Ensure the operation is atomic (all or nothing)
    public void approveRequest(Long id) {
        Optional<Admin> adminOptional = adminRepository.findById(id);
        if (adminOptional.isPresent()) {
            Admin signupRequest = adminOptional.get();
            // Only allow approval if the request is currently PENDING
            if (Status.PENDING.equals(signupRequest.getStatus())) {
                signupRequest.setStatus(Status.APPROVED); // Set status using the enum
                adminRepository.save(signupRequest);
                // TODO: In a real microservice, publish an event here (e.g., UserApprovedEvent)
                System.out.println("Signup request ID: " + id + " approved. (Event publish placeholder)");
            } else {
                // Custom exception could be more specific (e.g., RequestAlreadyProcessedException)
                throw new IllegalStateException("Request ID " + id + " is not pending. Current status: " + signupRequest.getStatus());
            }
        } else {
            // Custom exception could be more specific (e.g., ResourceNotFoundException)
            throw new IllegalArgumentException("Signup request with ID " + id + " not found.");
        }
    }

    @Override
    @Transactional // Ensure the operation is atomic
    public void rejectRequest(Long id) {
        Optional<Admin> adminOptional = adminRepository.findById(id);
        if (adminOptional.isPresent()) {
            Admin signupRequest = adminOptional.get();
            // Only allow rejection if the request is currently PENDING
            if (Status.PENDING.equals(signupRequest.getStatus())) {
                signupRequest.setStatus(Status.REJECTED); // Set status using the enum
                adminRepository.save(signupRequest);
                // TODO: In a real microservice, publish an event here (e.g., UserRejectedEvent)
                System.out.println("Signup request ID: " + id + " rejected. (Event publish placeholder)");
            } else {
                throw new IllegalStateException("Request ID " + id + " is not pending. Current status: " + signupRequest.getStatus());
            }
        } else {
            throw new IllegalArgumentException("Signup request with ID " + id + " not found.");
        }
    }

    @Override
    @Transactional
    public Admin createSignupRequest(AddRequestDto addRequestDto) {
        Admin newRequest= ToAdminMapper.toAdmin(addRequestDto);
        return adminRepository.save(newRequest);
    }

    @Override
    public List<Admin> listAllRequests() {
        return adminRepository.findAll();
    }
}