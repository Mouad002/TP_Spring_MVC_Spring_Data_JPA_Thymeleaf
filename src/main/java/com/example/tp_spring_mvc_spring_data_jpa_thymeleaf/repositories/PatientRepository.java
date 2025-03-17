package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.repositories;

import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    public Page<Patient> findByNomContains(String nom, Pageable pageable);
    @Query("select p from Patient p where p.nom like :x")
    public Page<Patient> search(@Param("x") String nom, Pageable pageable);
}

