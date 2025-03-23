package cn.dumboj.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.*;
import cn.dumboj.domain.User;

@RestController
@RequestMapping("/restful")
public class HttpRestController {
    @GetMapping("/get")
    public String getM() {
        return "this is Get Method";
    }
    @PostMapping("/getWithName")
    public String getWithName(@RequestParam String name, @RequestBody Profile profile) {
        return "this is POST Method" + name + profile.gender;
    }
    @PutMapping("/putName/{name}")
    public String getWithName(@PathVariable String name) {
        return "this is PUT Method" + name;
    }
    @Data
    private class Profile {
        private String gender;
        private boolean vip;
    }
}
