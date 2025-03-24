package cn.dumboj.security.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

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
