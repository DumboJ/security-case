package cn.dumboj.controller;

import cn.dumboj.utils.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/login/")
public class WebController {
    @GetMapping("index")
    public String visitor() {
        return "Hello Security";
    }

    @GetMapping("/principal")
    public String getPrincipal(Principal principal) {
        return principal.getName();
    }
    @GetMapping("/getAuthentication")
    public Authentication getAuthentication(Authentication authentication) {
        return authentication;
    }
    @GetMapping("/anonymous")
    public String getAnonymous() {
        return SecurityUtils.getCurrentLogin();
    }
    @GetMapping("/Me")
    public String getCurrent() {
        return SecurityUtils.getCurrentLogin();
    }
}
