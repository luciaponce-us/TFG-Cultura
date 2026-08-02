package com.tfg.cultura.api.seeder.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;

import com.tfg.cultura.api.seeder.dto.UserCsvRow;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCsvParser {
    private static final String CSV_FILE_PATH = "data/users.csv";
    private final PasswordEncoder passwordEncoder;

    public List<User> loadUsersFromCsv() {
        Resource resource = new ClassPathResource(CSV_FILE_PATH);

        try (InputStream is = resource.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, StandardCharsets.UTF_8))) {

            return reader.lines()
                    .skip(1)
                    .map(this::mapLineToUser)
                    .map(this::toUser)
                    .toList();

        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo " + CSV_FILE_PATH, e);
        }
    }

    private UserCsvRow mapLineToUser(String line) {
        String[] parts = line.split(",");

        return UserCsvRow.builder()
                .username(parts[0])
                .password(parts[1])
                .name(parts[2])
                .surname(parts[3])
                .dni(parts[4])
                .phone(parts[5])
                .email(parts[6])
                .role(parts[7])
                .paymentReceipt(parts[8])
                .active(Boolean.parseBoolean(parts[9]))
                .build();
    }

    private User toUser(UserCsvRow row) {
        return User.builder()
                .username(row.getUsername())
                .password(passwordEncoder.encode(row.getPassword()))
                .name(row.getName())
                .surname(row.getSurname())
                .dni(row.getDni())
                .phone(row.getPhone())
                .email(row.getEmail())
                .paymentReceipt(row.getPaymentReceipt())
                .active(row.isActive())
                .role(Role.valueOf(row.getRole()))
                .createdAt(LocalDateTime.now(ZoneId.of("Europe/Madrid")))
                .build();
    }
}
