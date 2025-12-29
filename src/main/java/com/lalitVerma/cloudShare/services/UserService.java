package com.lalitVerma.cloudShare.services;

import com.lalitVerma.cloudShare.dto.UserDTO;
import com.lalitVerma.cloudShare.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    // Create user
    UserDTO createUser(User user);

    // Update User
    UserDTO updateUser(String userId, UserDTO userDTO);

    User getUserById(String userId);

    // Get User by email
    UserDTO getUserDTOByEmail(String email);

    // Delete user
    void deleteUser(String userId);

}
