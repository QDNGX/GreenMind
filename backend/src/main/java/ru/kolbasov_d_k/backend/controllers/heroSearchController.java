package ru.kolbasov_d_k.backend.controllers;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class heroSearchController {

    @GetMapping("/search")
    public String search() {
        System.out.println("search");
        return "redirect:/register.html";
    }
}
