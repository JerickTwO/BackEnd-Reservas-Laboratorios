package com.masache.masachetesis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("saludar")
@RestController
public class SaludarController {

    @GetMapping
    public String hola() {
        return "Hola Mundo";
    }
}
