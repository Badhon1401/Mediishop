package com.sda_project.medishop.application.interfaces;

import com.sda_project.medishop.domain.UserVerificationMessage;

public interface UserVerificationMessageRepository {
    void deleteByUserEmail( String email);

    UserVerificationMessage save(UserVerificationMessage verificationCode);

    UserVerificationMessage findByUserEmail(String email);

    void delete(UserVerificationMessage verificationMessage);
}
