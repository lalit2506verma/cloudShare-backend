package com.lalitVerma.cloudShare.services.impl;

import com.lalitVerma.cloudShare.dto.UserDTO;
import com.lalitVerma.cloudShare.entities.User;
import com.lalitVerma.cloudShare.exception.DuplicateUserExistException;
import com.lalitVerma.cloudShare.exception.UserNotFoundException;
import com.lalitVerma.cloudShare.repository.UserRepository;
import com.lalitVerma.cloudShare.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public final int INITIAL_CREDIT = 5;
    private final UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @Override
    public UserDTO createUser(User user) {

        // validation
        User NewUser = getUserByEmail(user.getEmail());

        if(NewUser != null){
            // user already exist with this email ID
            throw new DuplicateUserExistException("User with email " + user.getEmail() + " already exists");
        }

        // User can be created
        NewUser = new User();
        NewUser.setEmail(user.getEmail());
        NewUser.setFirstName(user.getFirstName());
        NewUser.setLastName(user.getLastName());
        NewUser.setCredits(INITIAL_CREDIT);
        NewUser.setCreatedAt(Instant.now());
        NewUser.setPhotoURL(user.getPhotoURL());
        NewUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // save
        NewUser =  userRepository.save(NewUser);

        // return DTO
        return new UserDTO(NewUser);
    }

    @Override
    public UserDTO updateUser(String userId, UserDTO userDTO) {

        User existingUser = getUserById(userId);

        if(existingUser != null){
            // Update the user data
            if(userDTO.getFirstName() != null && !userDTO.getFirstName().isEmpty()){
                existingUser.setFirstName(userDTO.getFirstName());
            }

            if(userDTO.getLastName() != null && !userDTO.getLastName().isEmpty()){
                existingUser.setLastName(userDTO.getLastName());
            }

            if(userDTO.getPhotoURL() != null && !userDTO.getPhotoURL().isEmpty()){
                existingUser.setPhotoURL(userDTO.getPhotoURL());
            }

            existingUser = this.userRepository.save(existingUser);
            return new UserDTO(existingUser);
        }

        throw new UserNotFoundException("User with id " + userDTO.getId() + " does not exist");

    }

    @Override
    public void deleteUser(String userId) {
        if(userRepository.existsById(userId)){
            userRepository.deleteById(userId);
        }
        else{
            throw new UserNotFoundException("User with id " + userId + " does not exist");
        }
    }

    @Override
    public User getUserById(String userId) {
        return this.userRepository.findById(userId).orElse(null);
    }

    @Override
    public UserDTO getUserDTOByEmail(String email) {
        return null;
    }

    public User getUserByEmail(String email){
        if(email != null){
            return this.userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }
}
