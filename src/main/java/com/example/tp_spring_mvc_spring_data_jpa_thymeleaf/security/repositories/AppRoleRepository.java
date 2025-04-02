package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.repositories;

import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, String> {

}
