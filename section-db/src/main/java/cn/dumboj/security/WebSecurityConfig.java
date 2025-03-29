package cn.dumboj.security;

import cn.dumboj.service.UserDetailsPasswordServiceImpl;
import cn.dumboj.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private UserDetailsPasswordServiceImpl userDetailPasswordServiceImpl;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.
                userDetailsService(userDetailsService)
                //自定义密码编码器
                .passwordEncoder(passwordEncoding())
                //密码升级服务
                .userDetailsPasswordManager(userDetailPasswordServiceImpl)
        ;
    }



    private PasswordEncoder passwordEncoding() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterAt(getCustomizerFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin(form ->
                        form.loginPage("/login/")
                                .successHandler(onCustomizeSuccessHandler())
                                .failureHandler(onCustomizerFailureHandler())
                                .permitAll()
                )
                .authorizeRequests(
                        req ->
                                //除了 /login 页都需要验证
                                req.antMatchers("/login/**").permitAll()
                                        .anyRequest().authenticated()
                )
                .logout(
                        logout ->
                                logout.logoutUrl("/perform_logout")
                )
                .csrf(csrf ->
                        csrf.ignoringAntMatchers("/login/**"))
        ;

    }

    private CustomizerFilter getCustomizerFilter() {
        CustomizerFilter customizerFilter = new CustomizerFilter();
        customizerFilter.setAuthenticationSuccessHandler(onCustomizeSuccessHandler());
        customizerFilter.setAuthenticationFailureHandler(onCustomizerFailureHandler());
        return customizerFilter;
    }

    /**
     * 处理失败时 把 异常 信息封装json返回
     */
    private AuthenticationFailureHandler onCustomizerFailureHandler() {
        return (request, response, exp) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            HashMap<String, String> resContent = new HashMap<>();
            resContent.put("title", "认证失败");
            resContent.put("msg", exp.getMessage());

            response.getWriter().println(objectMapper.writeValueAsString(resContent));
        };
    }

    /**
     * 处理成功时 把 auth 信息返回
     */
    private AuthenticationSuccessHandler onCustomizeSuccessHandler() {
        return (request, response, auth) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().println(objectMapper.writeValueAsString(auth));
        };
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/static/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
