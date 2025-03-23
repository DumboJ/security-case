package cn.dumboj.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
    //localhost:8080/api/greeting
    @GetMapping("/greeting")
    public String greeting() {
        return "First Spring Security";
    }
}
