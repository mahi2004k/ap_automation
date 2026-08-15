package com.ap_automation.controller;

import com.ap_automation.dto.request.LoginRequest;
import com.ap_automation.dto.request.RefreshTokenRequest;
import com.ap_automation.dto.request.RegisterRequest;
import com.ap_automation.dto.response.AuthResponse;
import com.ap_automation.dto.response.LoginResponse;
import com.ap_automation.dto.response.RegisterResponse;
import com.ap_automation.entity.RefreshToken;
import com.ap_automation.entity.User;
import com.ap_automation.security.CustomUserDetailsService;
import com.ap_automation.security.JwtService;
import com.ap_automation.service.AuthService;
import com.ap_automation.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final RefreshTokenService refreshTokenService;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtService jwtService;

    @PostMapping("/register")
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request
    ){
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request
    ){
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.findByToken(
                        request.getRefreshToken()
                );

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String newAccessToken =
                jwtService.generateToken(userDetails);

        return ResponseEntity.ok(
                new AuthResponse(
                        newAccessToken,
                        refreshToken.getToken()
                )
        );
    }
}
