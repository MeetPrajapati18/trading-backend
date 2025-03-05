package com.meet.trading.repository;

import com.meet.trading.model.ForgotPasswordToken;
import com.meet.trading.service.ForgotPasswordService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken,String> {
    ForgotPasswordToken findByUserId(Long userId);
}
