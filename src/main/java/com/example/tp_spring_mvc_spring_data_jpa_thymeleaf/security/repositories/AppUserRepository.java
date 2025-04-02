package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.repositories;

import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
    AppUser findByUsername(String username);
}
