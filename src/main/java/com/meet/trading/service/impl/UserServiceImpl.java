package com.meet.trading.service.impl;

import com.meet.trading.config.JwtProvider;
import com.meet.trading.domain.VerificationType;
import com.meet.trading.model.TwoFactorAuth;
import com.meet.trading.model.User;
import com.meet.trading.repository.UserRepository;
import com.meet.trading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
//@RestController

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findUserProfileByJwt(String jwt) throws Exception {
        String email = JwtProvider.getEmailFromToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user == null){
            throw new Exception("User not found by token");
        }

        return user;
    }

    @Override
    public User findUserProfileByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);

        if(user == null){
            throw new Exception("User not found by Email.");
        }

        return user;
    }

    @Override
    public User findUserById(Long userId) throws Exception {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()){
            throw new Exception("User not found by id.");
        }

        return user.get();
    }

    @Override
    public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSendTo(verificationType);

        user.setTwoFactorAuth(twoFactorAuth);

        return userRepository.save(user);
    }

    @Override
    public User UpdatePassword(User user, String newPassword) {

        String hashedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }
}
