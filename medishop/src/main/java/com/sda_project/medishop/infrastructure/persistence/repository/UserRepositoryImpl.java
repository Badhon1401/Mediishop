package com.sda_project.medishop.infrastructure.persistence.repository;

import com.sda_project.medishop.application.interfaces.UserRepository;
import com.sda_project.medishop.domain.User;
import com.sda_project.medishop.infrastructure.service.DomainMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Autowired
    public UserRepositoryImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        return DomainMapperService.mapToUserDomain(userJpaRepository.save(DomainMapperService.mapToUserJpaEntity(user)));
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return userJpaRepository.findById(userId)
                .map(DomainMapperService::mapToUserDomain);
    }

    @Override
    public Optional<User> findByUserName(String username) {
        return userJpaRepository.findByUserName(username)
                .map(DomainMapperService::mapToUserDomain);
    }

    @Override
    public List<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .stream()
                .map(DomainMapperService::mapToUserDomain)
                .collect(Collectors.toList());
    }
}
