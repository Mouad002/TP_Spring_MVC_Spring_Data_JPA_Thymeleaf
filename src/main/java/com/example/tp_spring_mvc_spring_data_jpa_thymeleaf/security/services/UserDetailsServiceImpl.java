package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.services;

import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security.entities.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private AccountService accountService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = accountService.loadUserByUsername(username);
        if(appUser == null) throw new UsernameNotFoundException(String.format("User %s not found",username));
        UserDetails userDetails = User
                .withUsername(username)
                .password(appUser.getPassword())
                .roles(appUser.getRoles().stream().map(e -> e.getRole()).toArray(String[]::new))
                .build();
        return userDetails;
    }
}
