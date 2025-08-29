package com.security.ShopLite.controller;

//login/signup endpoints

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.security.ShopLite.config.JwtUtil;
import com.security.ShopLite.model.UserEntity;
import com.security.ShopLite.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    // DTOs
    record LoginRequest(String username, String password) {}
    record SignupRequest(String username, String password) {}
    record JwtResponse(String token) {}

    // Signup endpoint (default role = CUSTOMER)
    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest req) {
        if (userRepo.findByUsername(req.username()).isPresent()) {
            return "❌ Username already exists!";
        }
        UserEntity newUser = new UserEntity();
        newUser.setUsername(req.username());
        newUser.setPassword(passwordEncoder.encode(req.password()));
        newUser.setRole("ROLE_CUSTOMER");
        newUser.setActive(true);
        userRepo.save(newUser);
        return "✅ User registered successfully!";
    }

    // Login endpoint → returns JWT
    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        UserEntity user = userRepo.findByUsername(req.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new JwtResponse(token);
    }
}
