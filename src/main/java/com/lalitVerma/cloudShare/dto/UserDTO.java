package com.lalitVerma.cloudShare.dto;

import com.lalitVerma.cloudShare.entities.User;

import java.time.Instant;

public class UserDTO {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private int credits;
    private String photoURL;
    private Instant createdAt;

    public UserDTO() {
    }

    public UserDTO(String id, String email, String firstName, String lastName, int credits, String photoURL, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.credits = credits;
        this.photoURL = photoURL;
        this.createdAt = createdAt;
    }

    public UserDTO(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCredits(),
                user.getPhotoURL(),
                user.getCreatedAt()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
