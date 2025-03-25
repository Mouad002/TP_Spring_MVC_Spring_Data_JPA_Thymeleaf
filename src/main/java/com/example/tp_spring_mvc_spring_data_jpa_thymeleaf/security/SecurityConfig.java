package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .formLogin(ar -> ar.loginPage("/login").permitAll())
                .rememberMe(rm -> rm.key("remember-me-key") // Enables remember-me feature
                        .tokenValiditySeconds(86400) // Token valid for 1 day (optional)
                )
                .exceptionHandling(ar -> ar.accessDeniedPage("/notAuthorized"))
                .authorizeHttpRequests(ar -> ar
                        .requestMatchers("/webjars/**", "/css/**", "/js/**", "/h2-console/**").permitAll()
//                        .requestMatchers("/user/**").hasRole("USER")
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user1").password(passwordEncoder.encode("1234")).roles("USER").build(),
                User.withUsername("user2").password(passwordEncoder.encode("1234")).roles("USER").build(),
                User.withUsername("admin").password(passwordEncoder.encode("1234")).roles("USER","ADMIN").build()
        );
    }
}
