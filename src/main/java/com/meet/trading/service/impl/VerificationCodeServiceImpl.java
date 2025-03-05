package com.meet.trading.service.impl;

import com.meet.trading.domain.VerificationType;
import com.meet.trading.model.User;
import com.meet.trading.model.VerificationCode;
import com.meet.trading.repository.VerificationCodeRepository;
import com.meet.trading.service.VerificationCodeService;
import com.meet.trading.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Override
    public VerificationCode sendVerificationCode(User user, VerificationType verificationType) {

        VerificationCode verificationCodeSaved = new VerificationCode();
        verificationCodeSaved.setOtp(OtpUtils.generateOTP());
        verificationCodeSaved.setVerificationType(verificationType);
        verificationCodeSaved.setUser(user);

        return verificationCodeRepository.save(verificationCodeSaved);
    }

    @Override
    public VerificationCode getVerificationCodeById(Long id) throws Exception {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findById(id);

        if (verificationCode.isPresent()) {
            return verificationCode.get();
        }

        throw new Exception("Verification Code Not Found");
    }

    @Override
    public VerificationCode getVerificationCodeByUser(Long userId) {
        return verificationCodeRepository.findByUserId(userId);
    }

    @Override
    public void deleteVerificationCodeById(VerificationCode verificationCode) {

    }
}
