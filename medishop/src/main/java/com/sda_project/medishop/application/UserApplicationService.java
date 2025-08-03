package com.sda_project.medishop.application;

import com.sda_project.medishop.application.interfaces.UserRepository;
import com.sda_project.medishop.application.interfaces.UserVerificationMessageRepository;
import com.sda_project.medishop.domain.User;
import com.sda_project.medishop.domain.UserVerificationMessage;
import com.sda_project.medishop.domain.exception.UserAlreadyExistsException;
import com.sda_project.medishop.infrastructure.service.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class UserApplicationService {
    private final UserRepository userRepository;
    private final UserVerificationMessageRepository verificationMessageRepository;

    private final AuthenticationManager authManager;

    private final JWTService jwtService;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    public UserApplicationService(UserRepository userRepository, UserVerificationMessageRepository verificationMessageRepository, AuthenticationManager authManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.verificationMessageRepository = verificationMessageRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }
    @Transactional
    public UserVerificationMessage initiateRegistration(User user) {
        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists.");
        }
        UserVerificationMessage verificationMessage=generateVerificationCode(user.getEmail());
        verificationMessageRepository.deleteByUserEmail(user.getEmail());

        verificationMessageRepository.save(verificationMessage);
        return verificationMessage;

    }
    @Transactional
    public ResponseEntity<?> verifyVerificationCodeAndCompleteRegistration(User user, String code) {

        UserVerificationMessage verificationMessage = verificationMessageRepository.findByUserEmail(user.getEmail());
        if (verificationMessage == null ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No verification message found for this email");
        }
        if(verificationMessage.getExpiry().before(new Date())){
            verificationMessageRepository.delete(verificationMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expired verification code.");
        }
        if(!verificationMessage.getCode().equals(code)){
            verificationMessageRepository.delete(verificationMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong verification code.Previous code became useless. Ask for new code if required. ");
        }

        if (!user.getEmail().equals(verificationMessage.getUserEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email mismatch during verification.Please send the same credentials as before getting the verification code");
        }

        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            verificationMessageRepository.delete(verificationMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorry you have to register again with another name as in the mean time a new user with that user name registered");
        }

        verificationMessageRepository.delete(verificationMessage);

        String rawPassword = user.getPassword();

        user.setPassword(encoder.encode(rawPassword));

        userRepository.save(user);

        return performUserLogin(user.getUserName(),rawPassword);
    }

    public  ResponseEntity<?> performUserLogin(String userName,String password) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userName, password)
            );

            if (authentication.isAuthenticated()) {
                Optional<User> authenticatedUser = userRepository.findByUserName(userName);
                return authenticatedUser.map(user -> ResponseEntity.ok(jwtService.generateToken(user.getId(), user.getUserName(), user.getEmail()))).orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found in the database."));


            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authentication failed. Invalid credentials.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authentication failed. Bad credentials.");
        }
    }
    @Transactional
    public Map<String, Object> handleForgotUserCredentials(String email) {
        List<User> users = userRepository.findByEmail(email);
        if (users.isEmpty()) {
            return null;
        }

        UserVerificationMessage verificationMessage = generateVerificationCode(email);


        verificationMessageRepository.deleteByUserEmail(email);


        verificationMessageRepository.save(verificationMessage);

        Set<String> matchedUserNames=new HashSet<>();
        for(User user:users){
            matchedUserNames.add(user.getUserName());
        }
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("usernames", matchedUserNames);
        responseBody.put("verificationMessage", verificationMessage);

        return responseBody;

    }
    @Transactional
    public  ResponseEntity<?> verifyVerificationCodeForAccountVerification(String code,String userEmail,String userName,String updatedPassword) {

        UserVerificationMessage verificationCode = verificationMessageRepository.findByUserEmail(userEmail);
        if (verificationCode == null ) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No verification code found for this email");
        }


        if(verificationCode.getExpiry().before(new Date())){
            verificationMessageRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expired verification code.");
        }
        if (!verificationCode.getCode().equals(code))
        {
            verificationMessageRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Wrong verification code. Previous code became useless. Ask for new code if required.");
        }

        Optional<User> userOptional = userRepository.findByUserName(userName);


        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }


        User user = userOptional.get();
        user.setPassword(encoder.encode(updatedPassword));
        userRepository.save(user);
        verificationMessageRepository.delete(verificationCode);
        return performUserLogin(userName,updatedPassword);

    }
    public ResponseEntity<?> getUserAccountDetails(UUID userId) {

        Optional<User> userOptional=userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }


        User user = userOptional.get();

        return ResponseEntity.ok(user);
    }

    private static UserVerificationMessage generateVerificationCode(String userEmail) {
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        return new UserVerificationMessage(
                code,
                userEmail,
                new Date(System.currentTimeMillis() + (7 * 60 * 1000))
        );

    }

}
