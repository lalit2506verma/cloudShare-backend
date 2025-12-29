package com.lalitVerma.cloudShare.controller;

import com.lalitVerma.cloudShare.dto.LoginRequestDto;
import com.lalitVerma.cloudShare.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public String loginUser(@RequestBody LoginRequestDto loginRequestDto) throws Exception {
        return authService.login(loginRequestDto);
    }
}
