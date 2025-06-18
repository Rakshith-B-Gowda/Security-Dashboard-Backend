package com.jpmc.admin_service.model;

import jakarta.persistence.*;
import lombok.Data; // While present, if you write getters/setters manually, @Data becomes redundant for them

@Entity
@Table(name = "signup_requests")
@Data // Keeping @Data, but know it will cause IDE warnings/redundancy with manual methods
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String requestedRole; // e.g., "read", "read/upload"

    @Enumerated(EnumType.STRING) // Stores the enum's name (String) in the database
    private Status status; // Now uses the Status enum

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRequestedRole() {
        return requestedRole;
    }

    public void setRequestedRole(String requestedRole) {
        this.requestedRole = requestedRole;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    // --- End of Manually Generated Getters and Setters ---
}