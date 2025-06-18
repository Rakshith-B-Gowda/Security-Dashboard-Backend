package com.jpmc.admin_service.repository;

import com.jpmc.admin_service.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    List<Admin> findByStatus(String status);

}
