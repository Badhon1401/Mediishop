package com.sda_project.medishop.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "medishop_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "contact_number")
    private String contactNumber;


    public UserJpaEntity(UUID id) {
        this.id=id;
    }

}

//eyJhbGciOiJIUzUxMiJ9.eyJlbWFpbCI6Im1ybWludXM0NDBAZ21haWwuY29tIiwidXNlcm5hbWUiOiJhYmIiLCJzdWIiOiJjNGMxYWY5Yy1lZWI5LTQyNGQtOGE4Mi1kZjdlZWNhYzkwNzUiLCJpYXQiOjE3NTA5MTQ4MTIsImV4cCI6MTc1MjcyOTIxMn0.dTE-6UE2kbAtFV-PsI9-ek-cFNnJEpGAcc15vYDVpuK0D-rWpIZ5CuepJYQ1A06QIiPA_vsgNRgwwLEuIJMoaQ