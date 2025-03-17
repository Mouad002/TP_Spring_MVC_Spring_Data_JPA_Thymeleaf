package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.web;

import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.entities.Patient;
import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PatientController {
    private PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }
    @GetMapping("/index")
    public String index(Model model, @RequestParam(value = "page", defaultValue = "0") int page ,@RequestParam(value = "size", defaultValue = "5") int size) {
        Page<Patient> patients = patientRepository.findAll(PageRequest.of(page,size));
        model.addAttribute("patients", patients.getContent());
        model.addAttribute("pages", new int[patients.getTotalPages()]);
        model.addAttribute("currentPage", page);
        return "patients";
    }
}
