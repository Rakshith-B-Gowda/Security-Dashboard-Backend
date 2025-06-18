package com.jpmc.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    private Permission permission;

    //public Long getId()              { return id; }
    //public void setId(Long id)       { this.id = id; }

    //public String getName()          { return name; }
    //public void setName(String name) { this.name = name; }

    //public String getEmail()         { return email; }
    //public void setEmail(String email) { this.email = email; }

    //public Permission getPermission()                 { return permission; }
    //public void setPermission(Permission permission)  { this.permission = permission; }

}
