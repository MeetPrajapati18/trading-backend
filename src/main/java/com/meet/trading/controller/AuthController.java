package com.meet.trading.controller;

import com.meet.trading.config.JwtProvider;
import com.meet.trading.model.TwoFactorAuth;
import com.meet.trading.model.TwoFactorOTP;
import com.meet.trading.model.User;
import com.meet.trading.repository.UserRepository;
import com.meet.trading.response.AuthResponse;
import com.meet.trading.service.CustomUserDetailsService;
import com.meet.trading.service.EmailService;
import com.meet.trading.service.TwoFactorOtpService;
import com.meet.trading.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {

        User isEmailExist = userRepository.findByEmail(user.getEmail());

        if (isEmailExist != null) {
            throw new Exception("User with this email already exist try different email to register.");
        }

        User newUser = new User();

        newUser.setFullName(user.getFullName()); // Set full name
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        // Explicitly set twoFactorAuth to prevent null
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnabled(false); // Default value
        newUser.setTwoFactorAuth(twoFactorAuth);

        User savedUser = userRepository.save(newUser);

        System.out.println("Saved User: " + savedUser);
        System.out.println("TwoFactorAuth Enabled: " + savedUser.getTwoFactorAuth().isEnabled());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = JwtProvider.generateToken(auth);

        AuthResponse res = new AuthResponse();

        res.setIsTwoFactorAuthEnabled(
                savedUser.getTwoFactorAuth() != null ? savedUser.getTwoFactorAuth().isEnabled() : false
        );
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("User Registered Successfully");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

//    @PostMapping("/signin")
//    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {
//
//        String email = user.getEmail();
//        String password = user.getPassword();
//
//        Authentication auth = authenticate(email, password);
//
//        SecurityContextHolder.getContext().setAuthentication(auth);
//        String jwt = JwtProvider.generateToken(auth);
//        User savedUser = userRepository.findByEmail(email);
//
//        if(user.getTwoFactorAuth().isEnabled()){
//            AuthResponse res = new AuthResponse();
//            res.setMessage("Two factor auth is enabled");
//            res.setIsTwoFactorAuthEnabled(true);
//            String otp = OtpUtils.generateOTP();
//
//            TwoFactorOTP oldTwoFactorOtp = twoFactorOtpService.findByUser(savedUser.getId());
//
//            if (oldTwoFactorOtp != null){
//                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOtp);
//            }
//
//            TwoFactorOTP newTwoFactorOtp = twoFactorOtpService.createTwoFactorOtp(savedUser, otp, jwt);
//
//            emailService.sendVerificationOtpEmail(email, otp);
//
//            res.setSession(newTwoFactorOtp.getId());
//            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
//
//        }
//
//        AuthResponse res = new AuthResponse();
//
//        res.setJwt(jwt);
//        res.setStatus(true);
//        res.setMessage("User logged in Successfully");
//
//        return new ResponseEntity<>(res, HttpStatus.CREATED);
//    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {
        String email = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = JwtProvider.generateToken(auth);

        User savedUser = userRepository.findByEmail(email);

        // Ensure TwoFactorAuth object is not null before checking isEnabled()
        if (savedUser.getTwoFactorAuth() != null && savedUser.getTwoFactorAuth().isEnabled()) {
            AuthResponse res = new AuthResponse();
            res.setMessage("Two factor auth is enabled");
            res.setIsTwoFactorAuthEnabled(true);
            String otp = OtpUtils.generateOTP();

            TwoFactorOTP oldTwoFactorOtp = twoFactorOtpService.findByUser(savedUser.getId());

            if (oldTwoFactorOtp != null) {
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOtp);
            }

            TwoFactorOTP newTwoFactorOtp = twoFactorOtpService.createTwoFactorOtp(savedUser, otp, jwt);
            emailService.sendVerificationOtpEmail(email, otp);

            res.setSession(newTwoFactorOtp.getId());
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        }

        // Normal login response if 2FA is disabled
        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("User logged in Successfully");
        res.setIsTwoFactorAuthEnabled(false); // Explicitly setting it to false when 2FA is disabled

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if(userDetails == null){
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySignInOtp(@PathVariable String otp, @RequestParam String id) throws Exception {
        
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findById(id);

        if(twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP, otp)) {
            AuthResponse res = new AuthResponse();
            res.setMessage("Two factor authentication verified.");
            res.setIsTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOTP.getJwt());

            return new ResponseEntity<>(res, HttpStatus.OK);
        }

        throw new Exception("Invalid OTP");
    }

}
