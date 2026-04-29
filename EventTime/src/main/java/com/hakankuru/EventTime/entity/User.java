package com.hakankuru.EventTime.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String email;
    private String password;

    private Boolean emailVerified;
    private String verificationCode;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}