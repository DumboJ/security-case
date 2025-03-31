package cn.dumboj.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Optional;

public class SecurityUtils {
    public static String getCurrentLogin() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> auth.getPrincipal())
                .map(cred -> {
                    //如果实现了 UserDetails 取用户名
                    if (cred instanceof UserDetails) {
                        return ((UserDetails) cred).getUsername();
                    }
                    //如果更多信息 看下是否为 Principal
                    if (cred instanceof Principal) {
                        return ((Principal) cred).getName();
                    }
                    //其它情况看作是 用户名 字符串
                    return String.valueOf(cred);
                })
                // 如果未认证，那么 Authentication 为 Null
                // 可以在未受安全保护的 URL 中实验
                // 此次返回匿名用户
                .orElse("anonymous");
    }
}
