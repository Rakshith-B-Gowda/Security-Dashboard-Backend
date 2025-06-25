package com.jpmc.admin_service.service;

import com.jpmc.admin_service.dto.AddRequestDto;
import com.jpmc.admin_service.model.Admin;

import java.util.List;

public interface AdminService {

    // Lists all pending signup requests
    List<Admin> listPendingRequests();

    // Allows admin to approve a signup request by ID
    void approveRequest(Long id);

    // Allows admin to reject a signup request by ID
    void rejectRequest(Long id);

    // Creates a new signup request (used internally or by event listener from UserService)
    Admin createSignupRequest(AddRequestDto addRequestDto);

    // Lists all signup requests regardless of status
    List<Admin> listAllRequests();

    List<Admin> listApprovedRequests();

    List<Admin> listRejectedRequests();
}