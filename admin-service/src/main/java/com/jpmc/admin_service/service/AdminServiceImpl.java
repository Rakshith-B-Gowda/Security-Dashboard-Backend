package com.jpmc.admin_service.service;

import com.jpmc.admin_service.dto.AddRequestDto;
import com.jpmc.admin_service.dto.NotificationDto;
import com.jpmc.admin_service.dto.UserRoleUpdateDto;
import com.jpmc.admin_service.enums.Status;
import com.jpmc.admin_service.mapper.ToAdminMapper;
import com.jpmc.admin_service.model.Admin;
import com.jpmc.admin_service.repository.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final WebClient.Builder webClientBuilder;

    public AdminServiceImpl(AdminRepository adminRepository, WebClient.Builder webClientBuilder) {
        this.adminRepository = adminRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public List<Admin> listPendingRequests() {
        return adminRepository.findByStatus(Status.PENDING);
    }

    @Override
    @Transactional
    public void approveRequest(Long id) {
        Optional<Admin> adminOptional = adminRepository.findById(id);
        if (adminOptional.isEmpty()) {
            throw new IllegalArgumentException("Signup request with ID " + id + " not found.");
        }

        Admin signupRequest = adminOptional.get();
        if (!Status.PENDING.equals(signupRequest.getStatus())) {
            throw new IllegalStateException("Request ID " + id + " is not pending. Current status: " + signupRequest.getStatus());
        }

        signupRequest.setStatus(Status.APPROVED);
        adminRepository.save(signupRequest);

        System.out.println("Signup request ID: " + id + " approved.");

        // Update role in user-service
        UserRoleUpdateDto updateDto = new UserRoleUpdateDto();
        updateDto.setEmail(signupRequest.getEmail());
        updateDto.setUpdateRole(signupRequest.getRequestedRole());

        webClientBuilder.build()
                .post()
                .uri("http://localhost:9093/user/admin/update")
                .bodyValue(updateDto)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();

        // Send notification
        NotificationDto notification = new NotificationDto();
        notification.setEmail(signupRequest.getEmail());
        notification.setMessage("Your request for role " + signupRequest.getRequestedRole() + " has been approved.");

        webClientBuilder.build()
                .post()
                .uri("http://localhost:9093/user/notification")
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

    @Override
    @Transactional
    public void rejectRequest(Long id) {
        Optional<Admin> adminOptional = adminRepository.findById(id);
        if (adminOptional.isEmpty()) {
            throw new IllegalArgumentException("Signup request with ID " + id + " not found.");
        }

        Admin signupRequest = adminOptional.get();
        if (!Status.PENDING.equals(signupRequest.getStatus())) {
            throw new IllegalStateException("Request ID " + id + " is not pending. Current status: " + signupRequest.getStatus());
        }

        signupRequest.setStatus(Status.REJECTED);
        adminRepository.save(signupRequest);
        System.out.println("Signup request ID: " + id + " rejected.");

        // Send rejection notification
        NotificationDto notification = new NotificationDto();
        notification.setEmail(signupRequest.getEmail());
        notification.setMessage("Your request for role " + signupRequest.getRequestedRole() + " has been rejected.");

        webClientBuilder.build()
                .post()
                .uri("http://localhost:9093/user/notification")
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

    @Override
    @Transactional
    public Admin createSignupRequest(AddRequestDto addRequestDto) {
        Admin newRequest = ToAdminMapper.toAdmin(addRequestDto);
        return adminRepository.save(newRequest);
    }

    @Override
    public List<Admin> listAllRequests() {
        return adminRepository.findAll();
    }
}
