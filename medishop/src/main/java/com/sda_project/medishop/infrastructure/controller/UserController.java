package com.sda_project.medishop.infrastructure.controller;


import com.sda_project.medishop.infrastructure.dto.UserRequestDto;
import com.sda_project.medishop.infrastructure.service.UserService;
import com.sda_project.medishop.infrastructure.utils.LoginCredentials;
import com.sda_project.medishop.infrastructure.utils.MediShopRegainAccountCredentials;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {


    @Autowired
    UserService userService;

    @PostMapping("/register/request")
    public ResponseEntity<?> initiateRegistration(@Valid @RequestBody UserRequestDto user) {
       return userService.initiateRegistration(user);
    }

    @PostMapping("/register/complete")
    public ResponseEntity<?> completeRegistration(@Valid @RequestBody UserRequestDto user, @RequestParam String code) {
         return userService.completeRegistration(user ,code);
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginCredentials loginCredentials) {
        System.out.println("UserName: "+loginCredentials.getUserName()+" PassWord: "+loginCredentials.getPassword());
         return userService.userLogin(loginCredentials);
    }

    @GetMapping("/login/forgot")
    public ResponseEntity<?> forgotUserCredentials(@RequestParam String userEmail) {
        return userService.forgotUserCredentials(userEmail);
    }

    @PostMapping("/login/forgot/verify")
    public ResponseEntity<?> forgottenAccountVerification(@RequestBody MediShopRegainAccountCredentials requestBody) {
        return userService.forgottenAccountVerification(
                requestBody.getCode(),
                requestBody.getUserEmail(),
                requestBody.getUserName(),
                requestBody.getUpdatedPassword()
        );
    }

    @GetMapping("/account/details/{userId}")
    public ResponseEntity<?> getAccountDetails(@PathVariable UUID userId) {
        return userService.getUserAccountDetails(userId);
    }


}