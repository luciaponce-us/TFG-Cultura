package com.tfg.cultura.api.users.model.dto;

import java.time.LocalDateTime;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

import lombok.Getter;

@Getter
public class UserResponse  {
    
    private String username;
    private String name;
    private String surname;
    private String dni;
    private String phone;
    private String email;
    private String avatar;
    private String paymentReceipt;
    private boolean active;
    private Role role;
    private LocalDateTime createdAt;

    public UserResponse(User user){
        this.username = user.getUsername();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.dni = user.getDni();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.paymentReceipt = user.getPaymentReceipt();
        this.active = user.isActive();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }

 }
