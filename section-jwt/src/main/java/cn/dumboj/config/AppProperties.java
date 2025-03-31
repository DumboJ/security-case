package cn.dumboj.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
/**
 * 配置类：JWT 的令牌访问和过期时间配置
 * */
@Configuration
@ConfigurationProperties(prefix = "dumbo")
@Valid
public class AppProperties {
    @Getter
    @Setter
    @Valid
    private Jwt jwt = new Jwt();

    @Data
    public static class Jwt {
        private String header = "Authorization";//Http 请求头中 key
        private String prefix = "Bearer "; //http 认证字段值前缀
        @Min(5000L)
        private long accessTokenExpireTime = 60 * 1000L;//访问令牌过期时间

        @Min(3 * 60 * 1000L)
        private long refreshTokenExpireTime = 1 * 60 * 24 * 1000;//刷新令牌过期时间

    }
}
