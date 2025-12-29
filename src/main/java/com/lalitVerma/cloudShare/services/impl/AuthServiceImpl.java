package com.lalitVerma.cloudShare.services.impl;

import com.lalitVerma.cloudShare.dto.LoginRequestDto;
import com.lalitVerma.cloudShare.entities.User;
import com.lalitVerma.cloudShare.repository.UserRepository;
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
    private final AuthUtils authUtils;

    @Override
    public String login(LoginRequestDto loginRequestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        return authUtils.generateAccessToken(user);
    }
}
