package com.prasanna.client.entity;

import jakarta.persistence.*;
import lombok.Data;

import javax.annotation.processing.Generated;

@Entity
@Data
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @Column(length=60)
    private String password;
    private String role;
    private boolean enabled=false;

}
