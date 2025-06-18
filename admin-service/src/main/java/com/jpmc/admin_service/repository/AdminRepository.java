package com.jpmc.admin_service.repository;

import com.jpmc.admin_service.model.Admin;
import com.jpmc.admin_service.model.Status; // Import the Status enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Best practice to add @Repository

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // Custom method to find requests by Status enum
    List<Admin> findByStatus(Status status); // Method signature updated to use Status enum
}