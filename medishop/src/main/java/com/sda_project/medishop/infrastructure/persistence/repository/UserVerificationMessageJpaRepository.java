package com.sda_project.medishop.infrastructure.persistence.repository;

import com.sda_project.medishop.infrastructure.persistence.entity.UserVerificationMessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface UserVerificationMessageJpaRepository extends JpaRepository<UserVerificationMessageJpaEntity, UUID> {
    UserVerificationMessageJpaEntity findByUserEmail(String userEmail);

    @Transactional
    void deleteByUserEmail(String userEmail);
}
