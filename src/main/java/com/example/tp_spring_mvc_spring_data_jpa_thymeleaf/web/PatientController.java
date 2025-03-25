package com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.web;

import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.entities.Patient;
import com.example.tp_spring_mvc_spring_data_jpa_thymeleaf.repositories.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PatientController {
    private PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping("/user/index")
    public String index(Model model,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "5") int size,
                        @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        Page<Patient> patients = patientRepository.findByNomContains(keyword, PageRequest.of(page,size));
        model.addAttribute("patients", patients.getContent());
        model.addAttribute("pages", new int[patients.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "patients";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/delete")
    public String delete(Model model, Long id, int page, String keyword) {
        patientRepository.deleteById(id);
        return "redirect:/user/index?page=" + page + "&keyword=" + keyword;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/edit")
    public String edit(Model model, Long id, String keyword, int page) {
        Patient p = patientRepository.findById(id).orElse(null);
        if(p==null) throw new RuntimeException("patient introuvable");
        model.addAttribute("patient", p);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        return "edit-patient";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/formPatients")
    public String formPatients(Model model) {
        model.addAttribute("patient",new Patient());
        return "form-patients";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(path = "/admin/save")
    public String save(Model model, @Valid Patient patient, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) return "form-patients";
        patientRepository.save(patient);
        return "redirect:/admin/formPatients";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(path = "/admin/editPatient")
    public String editPatient(Model model,
                              @Valid Patient patient,
                              BindingResult bindingResult,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "") String keyword) {
        System.out.println(patient);
        if(bindingResult.hasErrors()) return "form-patients";
        patientRepository.save(patient);
        return "redirect:/user/index?page="+page+"&keyword="+keyword;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/user/index";
    }
}
