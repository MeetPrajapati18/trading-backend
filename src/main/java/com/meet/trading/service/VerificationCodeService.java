package com.meet.trading.service;

import com.meet.trading.domain.VerificationType;
import com.meet.trading.model.User;
import com.meet.trading.model.VerificationCode;

public interface VerificationCodeService {

    VerificationCode sendVerificationCode(User user, VerificationType verificationType);

    VerificationCode getVerificationCodeById(Long id) throws Exception;

    VerificationCode getVerificationCodeByUser(Long userId);

    void deleteVerificationCodeById(VerificationCode verificationCode);

//    Boolean verifyOtp(String otp, VerificationCode verificationCode);
}
