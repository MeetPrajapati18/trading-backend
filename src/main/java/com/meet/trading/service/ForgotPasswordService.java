package com.meet.trading.service;

import com.meet.trading.domain.VerificationType;
import com.meet.trading.model.ForgotPasswordToken;
import com.meet.trading.model.User;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user,
                                    String id,
                                    String otp,
                                    VerificationType verificationType,
                                    String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);
}
