package com.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/index.html";
    }
    
    @GetMapping("/mobile")
    public String mobile() {
        return "redirect:/mobile.html";
    }
    
    @GetMapping("/docs")
    public String docs() {
        return "redirect:/docs.html";
    }
    
    @GetMapping("/documentation")
    public String documentation() {
        return "redirect:/docs.html";
    }
}