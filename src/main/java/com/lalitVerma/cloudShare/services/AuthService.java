package com.lalitVerma.cloudShare.services;

import com.lalitVerma.cloudShare.dto.LoginRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    String login(LoginRequestDto loginRequestDto);
}
