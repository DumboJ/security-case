package cn.dumboj.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf(csrf->csrf.disable())
            .formLogin(Customizer.withDefaults())
            .authorizeRequests(req ->
                    req.mvcMatchers("/api/greeting","/restful/*")
//                        .hasRole("ADMIN"));//验证角色 无：response code 403 request forbidden
            .authenticated()); //验证权限 request method url
    }
}
