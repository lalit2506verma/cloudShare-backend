package com.lalitVerma.cloudShare.controller;

import com.lalitVerma.cloudShare.dto.UserDTO;
import com.lalitVerma.cloudShare.entities.User;
import com.lalitVerma.cloudShare.services.UserCreditsService;
import com.lalitVerma.cloudShare.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserCreditsService userCreditsService;
    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void logDbName() {
        System.out.println("Connected MongoDB Database: " + mongoTemplate.getDb().getName());
    }

    @PostMapping("/")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        System.out.println(user);
        UserDTO savedUser = this.userService.createUser(user);

        if(savedUser == null){
            throw new RuntimeException("User not registered");
        }

        // Creating the UserCredits
        this.userCreditsService.createInitialCredits(savedUser.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UserDTO userDTO){

        UserDTO updatedUser = this.userService.updateUser(userId, userDTO);

        if(updatedUser == null){
            throw new RuntimeException("User not updated");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {

        if(userId != null && !userId.isEmpty()){
            this.userService.deleteUser(userId);
        }
        else{
            throw new IllegalArgumentException("Invalid user ID");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("User has been deleted");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        User user;
        if(userId != null && !userId.isEmpty()){
            user = this.userService.getUserById(userId);
        }
        else{
            throw new IllegalArgumentException("Invalid user ID");
        }
        UserDTO userDTO = new UserDTO(user);
        return ResponseEntity.ok(userDTO);
    }

}
