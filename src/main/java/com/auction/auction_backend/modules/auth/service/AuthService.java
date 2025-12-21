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

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateAccessToken(authentication);

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

        return AuthResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .role(userDetails.getAuthorities().iterator().next().getAuthority())
                .build();
    }
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = User.builder()
                .fullName(request.getFullname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.BIDDER)
                .active(true)
                .build();

        userRepository.save(user);
    }
}
