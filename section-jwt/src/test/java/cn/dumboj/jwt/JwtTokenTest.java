package cn.dumboj.jwt;

import cn.dumboj.config.AppProperties;
import cn.dumboj.domain.User;
import cn.dumboj.utils.JwtUtils;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Jwt token 校验
 *
 * @author DumboJ
 */
@ExtendWith(SpringExtension.class)
public class JwtTokenTest {
    private JwtUtils jwtUtils;

    @BeforeEach
    public void setup() {
    }
    @Test
    public void testGenJwt() {
        User user = new User();

    }
}
