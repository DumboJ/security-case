package cn.dumboj.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomizerFilter extends UsernamePasswordAuthenticationFilter {
    UsernamePasswordAuthenticationToken token;
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try (ServletInputStream inputStream = request.getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            //可订制不同的 token 与 filter 匹配
            token = new UsernamePasswordAuthenticationToken(jsonNode.get("username").textValue()
                                                    , jsonNode.get("password").textValue());

            setDetails(request, token);
            return this.getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new BadCredentialsException("检查请求体中是否存在用户信息，format:{\"username\":xx,\"password\":xx}");
        }
    }
}
