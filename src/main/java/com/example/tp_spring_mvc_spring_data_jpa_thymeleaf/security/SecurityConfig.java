package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .formLogin(ar -> ar.loginPage("/login").defaultSuccessUrl("/").permitAll())
                .rememberMe(rm -> rm.key("remember-me-key") // Enables remember-me feature
                        .tokenValiditySeconds(40000) // Token valid for 1 day (optional)
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
    CommandLineRunner commandLineRunner(JdbcUserDetailsManager jdbcUserDetailsManager) {
        return args -> {
            UserDetails ud1 = jdbcUserDetailsManager.loadUserByUsername("user11");
            UserDetails ud2 = jdbcUserDetailsManager.loadUserByUsername("user22");
            UserDetails ud3 = jdbcUserDetailsManager.loadUserByUsername("admin2");
            if(ud1 == null)
                jdbcUserDetailsManager.createUser(
                        User.withUsername("user11").password(passwordEncoder.encode("1234")).roles("USER").build()
                );
            if(ud2 == null)
                jdbcUserDetailsManager.createUser(
                        User.withUsername("user22").password(passwordEncoder.encode("1234")).roles("USER").build()
                );
            if(ud3 == null)
                jdbcUserDetailsManager.createUser(
                        User.withUsername("admin2").password(passwordEncoder.encode("1234")).roles("USER","ADMIN").build()
                );
        };
    }

//    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user1").password(passwordEncoder.encode("1234")).roles("USER").build(),
                User.withUsername("user2").password(passwordEncoder.encode("1234")).roles("USER").build(),
                User.withUsername("admin").password(passwordEncoder.encode("1234")).roles("USER","ADMIN").build()
        );
    }
}
