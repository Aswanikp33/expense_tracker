package com.expert_tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HomeController {
    @GetMapping("/")
    public String showIndexPage() {
        return "index";  // ✅ Loads index.html when visiting localhost:8080
    }
}
