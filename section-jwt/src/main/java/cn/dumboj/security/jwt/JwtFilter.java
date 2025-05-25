package cn.dumboj.security.jwt;

import cn.dumboj.config.AppProperties;
import cn.dumboj.utils.CollectionUtils;
import cn.dumboj.utils.JwtUtils;
import io.jsonwebtoken.*;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * 用于 JWT 的请求过滤器
 * */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;
    private final MapReactiveUserDetailsService mapReactiveUserDetailsService;

    JwtFilter(MapReactiveUserDetailsService mapReactiveUserDetailsService){
        appProperties = new AppProperties();
        this.mapReactiveUserDetailsService = mapReactiveUserDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (checkToken(request)) {//验证请求头信息是否符合规范

            // 验证 请求信息中的 token 信息是否包含必要权限集，是处理并通过验证，否则关闭验证
            validToken(request)
                    .filter(claims -> claims.get("authorities") != null)
                    .ifPresentOrElse(
                              //有值，设置SpringSecurity Authentication
                                this::setupSpringSecurity,
                                SecurityContextHolder::clearContext);
        }
        filterChain.doFilter(request, httpServletResponse);
    }

    /**
     * 有值时取出并设置 SpringSecurity
     * */
    private void setupSpringSecurity(Claims claims) {
        Object authorities = claims.get("authorities");
        List<?> authoritiesList = CollectionUtils.convertObjToList(authorities);
        val grantedAuthorities = authoritiesList.stream().map(String::valueOf)
                                                        .map(SimpleGrantedAuthority::new).collect(toList());
        //构建 认证 token
        val authentication = new UsernamePasswordAuthenticationToken(
                //使用 UserDetails 的 username 设置
                claims.getSubject(),
                null,
                grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 获取头信息中的 token
     * */
    private Optional<Claims> validToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(appProperties.getJwt().getHeader())
                .replace(appProperties.getJwt().getPrefix(), "");

        return JwtUtils.parseClaims(jwtToken, JwtUtils.accessKey);
        /*try {
            //获取的是 claims 中塞入的权限集合
            return Optional.of(JwtUtils.validateClaimsBody(jwtToken));
        } catch (ExpiredJwtException |UnsupportedJwtException|MalformedJwtException |SignatureException |IllegalArgumentException e) {
            return Optional.empty();
        }*/
    }

    /**
     * 验证头信息中认证头是否符合规范
     * Authorization: Bearer token
     * */
    private boolean checkToken(HttpServletRequest request) {
        String header = request.getHeader(appProperties.getJwt().getHeader());
        return header != null && header.startsWith(appProperties.getJwt()
                .getPrefix());
    }
}
