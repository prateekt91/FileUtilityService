package com.prat.fileutility.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AppController {

    @RequestMapping("/hello")
    public String hello() {
        return "Hello, GraalVM!";
    }
}
