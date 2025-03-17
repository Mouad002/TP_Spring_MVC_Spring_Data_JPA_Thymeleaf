package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.web;

import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.entities.Patient;
import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.repositories.PatientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PatientController {
    private PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }
    @GetMapping("/index")
    public String index(Model model) {
        List<Patient> patients = patientRepository.findAll();
        model.addAttribute("patients", patients);
        return "patients";
    }
}
