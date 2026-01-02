package com.lalitVerma.cloudShare.services;

import com.lalitVerma.cloudShare.dto.LoginRequest;
import com.lalitVerma.cloudShare.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    User login(LoginRequest loginRequest);
}
