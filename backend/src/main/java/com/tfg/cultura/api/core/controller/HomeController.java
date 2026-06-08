package com.tfg.cultura.api.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectToSwagger() {
        return "redirect:/api/docs";
    }

    @GetMapping({"/api", "/api/","/docs"})
    public String redirectApiToSwagger() {
        return "redirect:/api/docs";
    }
    
}
