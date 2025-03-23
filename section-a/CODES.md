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
  - ***Spring Security with HTTP configs***

