package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.repositories;

import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

}

