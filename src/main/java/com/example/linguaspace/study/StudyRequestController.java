
package com.example.linguaspace.study;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StudyRequestController {

    private final StudyRequestRepository repository;

    public StudyRequestController(StudyRequestRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/study-requests")
    public String showPage(Model model) {
        model.addAttribute("studyRequest", new StudyRequest());
        model.addAttribute(
                "requests",
                repository.findAllByOrderByCreatedAtDesc()
        );

        return "study-requests";
    }

    @PostMapping("/study-requests")
    public String save(@ModelAttribute StudyRequest studyRequest) {
        repository.save(studyRequest);

        return "redirect:/study-requests";
    }
}
