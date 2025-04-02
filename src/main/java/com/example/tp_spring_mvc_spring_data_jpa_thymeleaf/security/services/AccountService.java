package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.services;

import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.entities.AppRole;
import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.entities.AppUser;

public interface AccountService {
    AppUser addNewUser(String username, String password, String confirmPassword, String email);
    AppRole addNewRole(String role);
    void addRoleToUser(String username, String role);
    void removeRoleFromUser(String username, String role);
    AppUser loadUserByUsername(String username);
}
