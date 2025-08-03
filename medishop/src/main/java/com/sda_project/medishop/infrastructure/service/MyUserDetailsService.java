package com.sda_project.medishop.infrastructure.service;

import com.sda_project.medishop.domain.User;
import com.sda_project.medishop.infrastructure.persistence.repository.UserRepositoryImpl;
import com.sda_project.medishop.infrastructure.utils.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User not found with name: " + username));; // You must define this in your repository
        return new UserPrincipal(user);
    }

    public UserPrincipal loadUserByUserId(UUID userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        return new UserPrincipal(user);
    }
}
