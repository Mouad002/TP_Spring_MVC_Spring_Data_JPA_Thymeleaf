package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf;

import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.entities.Patient;
import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@SpringBootApplication
public class TpSpringMvcSpringDataJpaThymeleafApplication implements CommandLineRunner {
	@Autowired
	private PatientRepository patientRepository;

	public static void main(String[] args) {
		SpringApplication.run(TpSpringMvcSpringDataJpaThymeleafApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		Patient p1 = new Patient(null, "mouad", new Date(), false, 45);
//		Patient p2 = Patient.builder()
//			.nom("chaima")
//			.dateNaissance(new Date())
//			.malade(true)
//			.score(12)
//			.build();


//		patientRepository.save(p1);
//		patientRepository.save(p2);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
