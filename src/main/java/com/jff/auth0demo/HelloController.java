package com.jff.auth0demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Este es un endpoint público 🌐";
    }

    @GetMapping("/private")
    public String privateEndpoint() {
        return "Este es un endpoint protegido 🔐";
    }
}
