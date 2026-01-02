package com.lalitVerma.cloudShare.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private UserDTO userDto;
}
