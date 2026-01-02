package com.lalitVerma.cloudShare.controller;

import com.lalitVerma.cloudShare.dto.LoginRequest;
import com.lalitVerma.cloudShare.dto.LoginResponse;
import com.lalitVerma.cloudShare.dto.UserDTO;
import com.lalitVerma.cloudShare.entities.User;
import com.lalitVerma.cloudShare.security.AuthUtils;
import com.lalitVerma.cloudShare.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthUtils authUtils;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) throws Exception {
        // Authenticate user
        User user = authService.login(loginRequest);

        // Generate JWT token
        String token = authUtils.generateAccessToken(user);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserDto(new UserDTO(user));

        return ResponseEntity.ok(response);
    }
}
