package com.auction.auction_backend.modules.auth.service;

import com.auction.auction_backend.common.enums.UserRole;
import com.auction.auction_backend.modules.auth.dto.request.LoginRequest;
import com.auction.auction_backend.modules.auth.dto.request.RegisterRequest;
import com.auction.auction_backend.modules.auth.dto.response.AuthResponse;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import com.auction.auction_backend.security.jwt.JwtUtils;
import com.auction.auction_backend.security.userdetail.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final com.auction.auction_backend.modules.notification.service.EmailService emailService;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateAccessToken(authentication);

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

        return AuthResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .fullname(userDetails.getFullname())
                .role(userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""))
                .build();
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Generate 6 digit OTP
        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));
        System.out.println("======================================");
        System.out.println("OTP for " + request.getEmail() + ": " + otp);
        System.out.println("======================================");

        // Send OTP via Email
        emailService.sendOtpEmail(request.getEmail(), otp);

        User user = User.builder()
                .fullName(request.getFullname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.BIDDER)
                .active(false) // Deactive until verify
                .verificationCode(otp)
                .build();

        userRepository.save(user);
    }

    public AuthResponse verify(com.auction.auction_backend.modules.auth.dto.request.VerifyRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isActive()) {
            throw new RuntimeException("User already active");
        }

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        user.setActive(true);
        user.setVerificationCode(null);
        userRepository.save(user);

        // Auto login after verify
        return AuthResponse.builder()
                .token(jwtUtils.generateTokenFromUsername(user.getEmail())) // Simplified for now
                .id(user.getId())
                .email(user.getEmail())
                .fullname(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}
