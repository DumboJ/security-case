## program step and implement knowledge  with Spring Security

### Spring Security default filters
> 在 application.yml 配置中打开 spring Security 的 DEBUG 日志查看
>
    logging:
        level:
          org:
            springframework:
              security: DEBUG

> - org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilte
> - org.springframework.security.web.context.SecurityContextPersistenceFilter
> - org.springframework.security.web.header.HeaderWriterFilter
> - org.springframework.security.web.csrf.CsrfFilter
> - org.springframework.security.web.authentication.logout.LogoutFilter
> - org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
> - org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter
> - org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter
> - org.springframework.security.web.savedrequest.RequestCacheAwareFilter
> - org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter
> - org.springframework.security.web.authentication.AnonymousAuthenticationFilter
> - org.springframework.security.web.session.SessionManagementFilter
> - org.springframework.security.web.access.ExceptionTranslationFilter
> - org.springframework.security.web.access.intercept.FilterSecurityInterceptor

***

### First Step :
1. 引入依赖
```java
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
```
2. SpringSecurity 有默认配置登录页 **username**:user **password**: terminal console print
   eg:
   ```java
      Using generated security password: 8feb87e7-b133-4b74-961d-546504431dd7
    ```
3. SpringBoot 2.3.x 使用的SpringSecurity 5.3.2 支持使用 ***WebSecurityConfigurationAdapter*** 实现各类型配置
    - #### 重写  configure(HttpSecurity http)
    ```java
        public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
            @Override
            protected void configure(HttpSecurity http) throws Exception {
            http
            .formLogin(Customizer.withDefaults())
            .authorizeRequests(req -> req.mvcMatchers("/api/greeting")
            .hasRole("ADMIN"));//验证角色 无：response code 403 request forbidden
            //            .authenticated()); //验证权限 request method url
            }
         }
   ```
   
*** 
  - ***Spring Security HTTP configs***
    - > form 登录表单的配置 e.g: http.formLogin(form->form.loginPage("/login").permitAll()) 
      - 表单的配置具体查看 fromLogin 中的配置，前后端不分离时可以配置成功失败跳转页等
        - 需要配置登录页 permitAll() 不参与鉴权，否则会报多次重定向异常
      
          - 关于表单的登录状态自定义处理 
        
            - successHandler()
                ```java
                     /**
                      * 登录成功时处理的 handler ：把登录成功的auth 信息写成 json 返回
                      * */
                          private AuthenticationSuccessHandler onLoginSuccessHandler() {
                              return (req,res,auth)->{
                              ObjectMapper objMapper = new ObjectMapper();
                              res.setStatus(HttpStatus.OK.value());
                              res.getWriter().println(objMapper.writeValueAsString(auth));
                              };
                          }
                ```
          - failureHandler()
            ```java
                /**
                * 登录成功时处理的 handler ：把登录成功的auth 信息写成 json 返回
                * */
                  private AuthenticationSuccessHandler onLoginSuccessHandler() {
                  return (req,res,auth)->{
                          ObjectMapper objMapper = new ObjectMapper();
                          res.setStatus(HttpStatus.OK.value());
                          res.getWriter().println(objMapper.writeValueAsString(auth));
                      };
                  } 
            ```
  - > csrf 站点伪造攻击 具体可配置项查看 API (无状态登录：token 时无此问题)
  
  - > form 退出登录的配置 e.g: http.logout(logout-> logout.logoutUrl("/perform_logout"));
     
  - > rememberMe 记住登录态的配置 e.g: http.rememberMe(rm.tokenValiditySeconds(1*24*3600).rememberMeCookieName("TestRememberMe"))
    - **注意: Remember 时需要 UserDetailService 实例，此时需要配置  AuthenticationManagerBuilder 相关内容使 上下文中有管理对应的 UserDetailService**
  
      - 此时可修改 application.yml 中关于 security 的用户名密码配置使用代码配置。原配置
          ```yaml
              #不使用Spring Security 默认密码配置
              spring:
                security:
                  user:
                    name: user
                    password: 123456
                    roles: USER,ADMIN
          ```
      - 可使用密码加密算法处理密码值.修改后:
          **必须配置角色信息否则启动异常：授权不能为空**
        ```java
            @Override
            protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                  auth
                  .inMemoryAuthentication()
                  .withUser("user")
                  .password(passwordEncoder().encode("123456"))
                  .withRole("USER","ADMIN");
            }
            @Bean
            public PasswordEncoder passwordEncoder() {
                  return new BCryptPasswordEncoder();
            }
        ```
*** 
  - ***Spring Security AuthenticationManagerBuilder configs***
    - 认证管理相关的配置 与 UserDetailService 相关的内容，见前章描述

***    
  - ***Spring Security WebSecurity configs***
    - web 安全配置：静态资源访问配置
      ```java 
        /**
         * 配置 网页相关的安全配置
         * */
        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().mvcMatchers("/public/**")
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        }
      ```
*** 
  - ***查看UsernamePasswordAuthenticationFilter的实现逻辑***
  - ***实现一个自定义的 filter 处理特定类型（json）的 post 请求***
    1. 实现自定义 filter 
        ```java
          /*
           *  实现自定义的 filter
           *  同理微信/支付宝的验证规则也可以类似处理
           *
           * {@link UsernamePasswordAuthenticationFilter#attemptAuthentication}
           * */
           public class CustomizeFilter extends UsernamePasswordAuthenticationFilter {
               @Override
                public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
                                                            UsernamePasswordAuthenticationToken authRequest;
                     // 流读取 request 内容
                     try (InputStream ins = request.getInputStream()) {
                           ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode jsonNode = objectMapper.readTree(ins);
                           authRequest = new UsernamePasswordAuthenticationToken(
                           jsonNode.get("username").textValue(),
                           jsonNode.get("password").textValue());
                     } catch (IOException e) {
                       throw new BadCredentialsException("检查请求体中是否包含用户登录信息.");
                     }
                    setDetails(request, authRequest);
                   return this.getAuthenticationManager().authenticate(authRequest);
                }
           }  
       ```
    2. 实例化并配置自定义 filter :a-对于哪些路径生效;b-设置过滤器验证处理成功失败的 自定义 handler;c-由哪个验证管理器来处理验证
        ```java
          /**
           * 在自定义 WebSecurityConfigurerAdapter 的 http 中设置自定义 filter
           * */
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
       ```
    3. 在 HttpSecurity http 中把自定义过滤器添加到过滤器链中，使其生效
        ```java
          @Override
          protected void configure(HttpSecurity http) throws Exception {
             //对 HttpSecurity http 请求添加自定义 filter addFilterX() 有多种添加方式 at/before/after
                http.addFilterAt(getCustomizerFilter(), UsernamePasswordAuthenticationFilter.class);
          }  
       ```
    
