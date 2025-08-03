package com.sda_project.medishop.domain;

import java.util.UUID;

public class User {
    private UUID id;
    private String userName;
    private String email;
    private String password;
    private String contactNumber;

    public User(UUID id, String userName, String email, String password, String contactNumber) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
    }

    public User(UUID id) {
        this.id=id;
    }


    public UUID getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getContactNumber() {
        return contactNumber;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
