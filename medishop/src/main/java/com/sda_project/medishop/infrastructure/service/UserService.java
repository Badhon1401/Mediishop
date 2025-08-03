package com.sda_project.medishop.infrastructure.service;

import com.sda_project.medishop.application.UserApplicationService;
import com.sda_project.medishop.domain.UserVerificationMessage;
import com.sda_project.medishop.domain.exception.UserAlreadyExistsException;
import com.sda_project.medishop.infrastructure.dto.UserRequestDto;
import com.sda_project.medishop.infrastructure.persistence.repository.UserRepositoryImpl;
import com.sda_project.medishop.infrastructure.persistence.repository.UserVerificationMessageRepositoryImpl;
import com.sda_project.medishop.infrastructure.utils.LoginCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {


    private final UserVerificationMessageRepositoryImpl userVerificationMessageRepository;

    private final UserApplicationService applicationUserService;

    private final JWTService jwtService;

    private final AuthenticationManager authManager;

    @Autowired
    public UserService(UserRepositoryImpl userRepository, UserVerificationMessageRepositoryImpl userVerificationMessageRepository, JWTService jwtService, AuthenticationManager authManager) {
        this.userVerificationMessageRepository = userVerificationMessageRepository;
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.applicationUserService = new UserApplicationService(userRepository, userVerificationMessageRepository,authManager,jwtService);
    }


    public ResponseEntity<?> initiateRegistration(UserRequestDto user) {
          UserVerificationMessage verificationMessage;

        try {
               verificationMessage = applicationUserService.initiateRegistration(DomainMapperService.userDtoToUserDomain(user));

        } catch (UserAlreadyExistsException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());

        } catch (Exception e) {
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }

         StringBuilder emailStatus = new StringBuilder();

        try {
             String subject = "MediShop Registration Verification Code";
            String text = "Your MediShop account registration verification code is: " + verificationMessage.getCode() + ". It expires in 7 minutes.";

            SystemService.sendMessageThroughMail(subject, text, verificationMessage.getUserEmail());

            emailStatus.append("Email successfully sent to user email: ").append(verificationMessage.getUserEmail()).append("\n");

        } catch (Exception e) {
             emailStatus.append("Failed to send email to user email: ").append(verificationMessage.getUserEmail())
                    .append(": ").append(e.getMessage()).append("\n");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(emailStatus.toString());
        }

        return ResponseEntity.ok(emailStatus.toString());
    }

    public ResponseEntity<?> completeRegistration(UserRequestDto user, String code) {
        return applicationUserService.verifyVerificationCodeAndCompleteRegistration(DomainMapperService.userDtoToUserDomain(user), code);
    }

    public ResponseEntity<?> userLogin(LoginCredentials loginCredentials) {
         return applicationUserService.performUserLogin(loginCredentials.getUserName(), loginCredentials.getPassword());
    }

    public ResponseEntity<?> forgotUserCredentials(String userEmail) {
        Map<String, Object> response = applicationUserService.handleForgotUserCredentials(userEmail);
        if (response == null) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Set<String> matchedUserNames = (Set<String>) response.get("usernames");
        UserVerificationMessage verificationMessage = (UserVerificationMessage) response.get("verificationMessage");

        if (verificationMessage == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Verification message could not be generated.");
        }

        StringBuilder emailStatus = new StringBuilder();

        try {
            String subject = "MediShop Account Recovery - Verification Code & Usernames";
            StringBuilder text = new StringBuilder();
            text.append("Dear MediShop User,\n\n");
            text.append("We received a request to recover your MediShop account.\n\n");
            text.append("Your verification code is: ").append(verificationMessage.getCode()).append("\n");
            text.append("This code will expire in 7 minutes.\n\n");

            if (matchedUserNames != null && !matchedUserNames.isEmpty()) {
                text.append("The following usernames are associated with this email address:\n");
                for (String userName : matchedUserNames) {
                    text.append("- ").append(userName).append("\n");
                }
                text.append("\n");
            }

            text.append("If you did not request this account recovery, please ignore this email.\n\n");
            text.append("For your security, do not share this code with anyone.\n\n");
            text.append("Thank you for using MediShop!\n\n");
            text.append("Best regards,\n");
            text.append("The MediShop Support Team");

            SystemService.sendMessageThroughMail(subject, text.toString(), verificationMessage.getUserEmail());
             emailStatus.append("Email successfully sent to user email: ").append(verificationMessage.getUserEmail()).append("\n");

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("usernames", matchedUserNames);
            responseBody.put("emailStatus", emailStatus.toString());
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
             emailStatus.append("Failed to send email to user email: ").append(verificationMessage.getUserEmail())
                    .append(": ").append(e.getMessage()).append("\n");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(emailStatus.toString());
        }
    }

    public ResponseEntity<?> forgottenAccountVerification(String code, String userEmail, String userName, String updatedPassword) {
        return applicationUserService.verifyVerificationCodeForAccountVerification(code, userEmail, userName, updatedPassword);
    }

    public ResponseEntity<?> getUserAccountDetails(UUID userId) {
        return applicationUserService.getUserAccountDetails(userId);
    }

}