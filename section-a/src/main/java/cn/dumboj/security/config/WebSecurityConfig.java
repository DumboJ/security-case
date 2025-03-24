package cn.dumboj.security.config;

import cn.dumboj.security.filter.CustomizeFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    //表单提交的验证
    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                //指定跳转的登录页
                .formLogin(
                        form ->
                                form
                                        .loginPage("/login")
                                        .successHandler(onLoginSuccessHandler())
                                        .failureHandler(onLoginFailedHandler())
                                        .permitAll())
                .authorizeRequests(req ->
                                req.antMatchers("/api/greeting").authenticated()
//                        .hasRole("ADMIN"));//验证角色 无：response code 403 request forbidden
                )
                .logout(
                        logout ->
                                logout
                                        .logoutUrl("/perform_logout")
                                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .rememberMe(rm -> rm.tokenValiditySeconds(1 * 24 * 3600).rememberMeCookieName("TestRememberMe")); //验证权限 request method url
    }*/

    //自定义 filter 的配置
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf(csrf ->
                        csrf.ignoringAntMatchers("/customizer/**")
                )
                //指定跳转的登录页
                .formLogin(
                        form ->
                                form
                                        .loginPage("/login")
                                        .successHandler(onLoginSuccessHandler())
                                        .failureHandler(onLoginFailedHandler())
                                        .permitAll())
                .authorizeRequests(req ->
                                //这个路径要有角色
                                req.antMatchers("/api/greeting").hasRole("USER")
                                        //对应路径全部放行
                                        .antMatchers("/customizer/**").permitAll()
                                        .antMatchers("/restful/**").permitAll()
                                        .anyRequest().authenticated()
//                        .hasRole("ADMIN"));//验证角色 无：response code 403 request forbidden
                )
                //对 HttpSecurity http 请求添加自定义 filter addFilterX() 有多种添加方式 at/before/after
                .addFilterAt(getCustomizerFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout(
                        logout ->
                                logout
                                        .logoutUrl("/perform_logout")
                                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .rememberMe(rm -> rm.tokenValiditySeconds(1 * 24 * 3600).rememberMeCookieName("TestRememberMe")); //验证权限 request method url

    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return (req, res, auth) -> {
            ObjectMapper objMapper = new ObjectMapper();
            res.setStatus(HttpStatus.OK.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.setCharacterEncoding(StandardCharsets.UTF_8.name());
            Map<String, String> logoutContent = Map.of(
                    "title", "您登出",
                    "content", auth.toString() + ""
            );
            res.getWriter().println(objMapper.writeValueAsString(logoutContent));
        };
    }

    /**
     * 登录失败时处理的 handler ：把登录失败的 auth 信息写成 json 返回
     */
    private AuthenticationFailureHandler onLoginFailedHandler() {
        return (req, res, exp) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            /*val errorInfo = Map.of(
                    "title","认证失败",
                    "details",exp.getMessage()
            );*/
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put("title", "认证失败");
            errorInfo.put("details", exp.getMessage());
            res.getWriter().println(objectMapper.writeValueAsString(errorInfo));
        };
    }

    /**
     * 登录成功时处理的 handler ：把登录成功的auth 信息写成 json 返回
     */
    private AuthenticationSuccessHandler onLoginSuccessHandler() {
        return (req, res, auth) -> {
            ObjectMapper objMapper = new ObjectMapper();
            res.setStatus(HttpStatus.OK.value());
            res.getWriter().println(objMapper.writeValueAsString(auth));
        };
    }

    /**
     * 配置 网页相关的安全配置
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/public/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("user")
                .password(passwordEncoder().encode("123456"))
                // 必须配置，不然启动失败，提示 授权信息不能为空
                .roles("USER", "ADMIN");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 在自定义 WebSecurityConfigurerAdapter 的 http 中设置自定义 filter
     */
    private CustomizeFilter getCustomizerFilter() throws Exception {
        CustomizeFilter customizeFilter = new CustomizeFilter();
        // 同样可使用自定义验证成功失败的 handler
        customizeFilter.setAuthenticationSuccessHandler(onLoginSuccessHandler());
        customizeFilter.setAuthenticationFailureHandler(onLoginFailedHandler());
        customizeFilter.setAuthenticationManager(authenticationManager());
        //设置过滤 对哪些匹配的路径做处理
        customizeFilter.setFilterProcessesUrl("/customizer/**");
        return customizeFilter;
    }
}
