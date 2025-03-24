# setion-a

## part 1
* extends WebSecurityConfigurerAdapter
* * override functions
* * 
* ** 
* 
## part 2
 * customize filter 

## part 3 
* introduce utils like : 
 * * http client how to send request 

## 
Spring Security configs
## part 4 
 - issues 
 - Springboot3.x之后依赖的Spring Security  WebSecurityConfigurerAdapter，学习保证暂时使用SpringBoot2.3.x版本
 - 3.x 后推荐使用1. SecurityFilterChain Bean 来实现config(HttpSecurity http)方法配置
 - <p>3.x 后推荐使用2. AuthenticationManager Bean: 
        创建 UserDetailsService Bean 提供用户数据,
        然后使用AuthenticationManagerBuilder来构建 AuthenticationManager</p>
   