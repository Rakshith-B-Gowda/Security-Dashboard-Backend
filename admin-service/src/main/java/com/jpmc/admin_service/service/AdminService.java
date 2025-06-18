package com.jpmc.admin_service.service;

import com.jpmc.admin_service.model.Admin;

import java.util.List;

public interface AdminService {

    List<Admin> listPending();

    void approve(Long id);

    void reject(Long id);
}
