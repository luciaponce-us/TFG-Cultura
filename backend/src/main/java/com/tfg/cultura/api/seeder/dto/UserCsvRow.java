package com.tfg.cultura.api.seeder.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserCsvRow {
    private String username;
    private String password;
    private String name;
    private String surname;
    private String dni;
    private String phone;
    private String email;
    private String role;
    private String paymentReceipt;
    private boolean active;
}