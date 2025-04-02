package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.services;

import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.entities.AppRole;
import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.entities.AppUser;
import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.repositories.AppRoleRepository;
import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;
    @Override
    public AppUser addNewUser(String username, String password, String confirmPassword, String email) {
        AppUser user = appUserRepository.findByUsername(username);
        if (user != null) throw new RuntimeException("user already exists");
        if (!password.equals(confirmPassword)) throw  new RuntimeException("passwords not much");
        AppUser newUser = AppUser.builder()
                .userId(UUID.randomUUID().toString())
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();
        AppUser savedUser = appUserRepository.save(newUser);
        return savedUser;
    }

    @Override
    public AppRole addNewRole(String role) {
        AppRole appRole = appRoleRepository.findById(role).orElse(null);
        if (appRole != null) throw new RuntimeException("role already exists");
        return appRoleRepository.save(AppRole.builder().role(role).build());
    }

    @Override
    public void addRoleToUser(String username, String role) {
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findById(role).get();
        appUser.getRoles().add(appRole);
//        appUserRepository.save(appUser);
    }

    @Override
    public void removeRoleFromUser(String username, String role) {
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findById(role).get();
        appUser.getRoles().remove(appRole);
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }
}
