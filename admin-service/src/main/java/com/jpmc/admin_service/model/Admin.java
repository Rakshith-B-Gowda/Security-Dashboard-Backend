package com.jpmc.admin_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "signup_requests")
@Data
@Getter
@Setter
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    //private String password;
    private String requestedRole;
    private String status;


    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}