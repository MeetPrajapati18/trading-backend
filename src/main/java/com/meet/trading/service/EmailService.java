package com.meet.trading.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.hibernate.pretty.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender javaMailSender;

    public void sendVerificationOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

        String subject = "Verification OTP";
        String text = "<div style='font-family: Arial, sans-serif; text-align: center;'>" +
                "<h2 style='color: #333;'>Your Verification Code</h2>" +
                "<p style='font-size: 18px; color: #555;'>Use the following OTP to verify your account:</p>" +
                "<h3 style='background: #f3f3f3; padding: 10px; display: inline-block; border-radius: 5px;'>" + otp + "</h3>" +
                "<p style='font-size: 14px; color: #777;'>If you did not request this code, please ignore this email.</p>" +
                "</div>";

        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text, true);
        mimeMessageHelper.setTo(email);

        try {
            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new MailSendException(e.getMessage());
        }
    }
}
