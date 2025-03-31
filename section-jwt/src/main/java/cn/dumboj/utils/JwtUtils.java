package cn.dumboj.utils;

import cn.dumboj.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Component
public class JwtUtils {
    public static final Key accessKey = Keys.secretKeyFor(SignatureAlgorithm.ES512);
    public static final Key refreshKey = Keys.secretKeyFor(SignatureAlgorithm.ES512);

    @Autowired
    private AppProperties appProperties;

    public static Claims validateClaimsBody(String jwtToken) {
        return Jwts.parserBuilder().setSigningKey(JwtUtils.accessKey).build()
                .parseClaimsJws(jwtToken).getBody();
    }

    /**
     * 根据用户信息创建使用访问签名 生成的 jwt token
     *
     * @param userDetails 用户信息
     * @return jwt
     */
    public String createAccessToken(UserDetails userDetails) {
        return createAccessTokenWithExpTime(userDetails, appProperties.getJwt().getAccessTokenExpireTime());
    }
    /**
     * 根据用户信息创建使用访问签名 生成的 jwt token
     *
     * @param userDetails 用户信息
     * @return jwt
     */
    public String createRefreshToken(UserDetails userDetails) {
        return createRefreshTokenWithExpTime(userDetails, appProperties.getJwt().getRefreshTokenExpireTime());
    }

    public String createAccessTokenWithExpTime(UserDetails userDetails, long expireTime) {
        return createJwtToken(userDetails, expireTime, accessKey);
    }
    public String createRefreshTokenWithExpTime(UserDetails userDetails, long expireTime) {
        return createJwtToken(userDetails, expireTime, refreshKey);
    }
    /**
     * 根据用户信息和访问签名key生成一个 token
     * @param userDetails 用户信息
     * @param expireTime 过期时间
     * @param key  签名使用的key
     * @return jwt
     * */
    public String createJwtToken(UserDetails userDetails, long expireTime, Key key) {
        return Jwts.builder()
                .setId("dumboj")
                .setSubject(userDetails.getUsername())
                //权限信息可放在 claim 里后期取出做权限校验
                .claim("authorities", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(key, SignatureAlgorithm.ES512)
                .compact();
    }

    /**
     * 验签-访问签名
     * */
    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken,accessKey);
    }
    /**
     * 验签-刷新签名
     * */
    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, refreshKey);
    }

    public boolean validateToken(String token, Key signKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException| MalformedJwtException|SignatureException |IllegalArgumentException e) {
            return false;
        }
    }
    /**
     * validate  jwt token is expired
     * */
    public boolean validateNotExpired(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            if (e instanceof ExpiredJwtException) {
                return false;
            }
        }
        return true;
    }

    /**
     * build access token with refresh token
     * */
    public String buildAccessTokenWithRefreshToken(String refreshToken) {
        return parseClaims(refreshToken, refreshKey)
                .map(claims ->
                        Jwts.builder()
                                .setClaims(claims)
                                .setExpiration(new Date(System.currentTimeMillis() +
                                        appProperties.getJwt().getAccessTokenExpireTime()))
                                .signWith(accessKey)
                                .compact()
                ).orElseThrow();
    }

    /**
     * parse jwt's claims with different signKey
     * @param jwtToken
     * @param signKey  kinds of access and refresh
     * */
    public static Optional<Claims> parseClaims(String jwtToken, Key signKey) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(jwtToken).getBody();
            return Optional.of(claims);
        } catch (ExpiredJwtException | UnsupportedJwtException| MalformedJwtException|SignatureException |IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
