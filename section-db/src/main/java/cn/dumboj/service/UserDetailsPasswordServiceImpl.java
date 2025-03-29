package cn.dumboj.service;

import cn.dumboj.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 密码升级服务
 * */
@Transactional
@Service
public class UserDetailsPasswordServiceImpl implements UserDetailsPasswordService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        return userRepository.findOptionalByUsername(user.getUsername())
                .map(userFromDb -> userRepository.save(userFromDb.withPassword(newPassword)))
                .orElseThrow();
    }
}
