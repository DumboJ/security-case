package cn.dumboj.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 自定义 filter 处理的 Controller
 * */
@RestController
@RequestMapping("/customizer/")
public class CustomizerController {
    @PostMapping("/first")
    public String customizerFirst() {
        return "getFirst Data";
    }
}
