package com.auction.auction_backend.common.persistence;

import com.auction.auction_backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordResetter implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        resetPassword("admin@gmail.com");
        resetPassword("seller@gmail.com");
        resetPassword("bidder1@gmail.com");
        resetPassword("bidder2@gmail.com");
    }

    private void resetPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode("123456"));
            userRepository.save(user);
            log.info("Successfully reset password for user: {}", email);
        });
    }
}
