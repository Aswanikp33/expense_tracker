package com.expert_tracker.controller;

import com.expert_tracker.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        if (userService.isUsernameTaken(username)) {
            return "redirect:/register?error=true";
        }
        userService.registerUser(username, password);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
