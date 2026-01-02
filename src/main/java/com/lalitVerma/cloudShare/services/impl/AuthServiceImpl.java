package com.lalitVerma.cloudShare.services.impl;

import com.lalitVerma.cloudShare.dto.LoginRequest;
import com.lalitVerma.cloudShare.entities.User;
import com.lalitVerma.cloudShare.security.AuthUtils;
import com.lalitVerma.cloudShare.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    @Override
    public User login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        return (User) authentication.getPrincipal();
    }
}
