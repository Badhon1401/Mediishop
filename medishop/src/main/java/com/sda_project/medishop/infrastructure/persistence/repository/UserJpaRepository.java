package com.sda_project.medishop.infrastructure.persistence.repository;

import com.sda_project.medishop.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByUserName(String username);
    @Override
    Optional<UserJpaEntity> findById(UUID uuid);

    List<UserJpaEntity> findByEmail(String email);
}
