package com.meet.trading.service;

import com.meet.trading.domain.VerificationType;
import com.meet.trading.model.User;

public interface UserService {
    public User findUserProfileByJwt(String jwt) throws Exception;
    public User findUserProfileByEmail(String email) throws Exception;
    public User findUserById(Long userId) throws Exception;
    public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user);
    public User UpdatePassword(User user, String newPassword);
}
