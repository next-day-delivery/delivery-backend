package com.nextdaydelivery;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/api/health")
    public String hello() {
        return "Congratulation EC2 test success! version3";
    }
}

