package com.hakankuru.EventTime.controller;

import com.hakankuru.EventTime.dto.LoginRequest;
import com.hakankuru.EventTime.dto.RegisterRequest;
import com.hakankuru.EventTime.entity.Department;
import com.hakankuru.EventTime.entity.User;
import com.hakankuru.EventTime.repository.DepartmentRepository;
import com.hakankuru.EventTime.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // REGISTER
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        boolean exists = userRepository.findByEmail(request.email).isPresent();

        if (exists) {
            return "EMAIL_ALREADY_EXISTS";
        }

        Department department = departmentRepository.findById(request.departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        User user = new User();

        user.setName(request.name);
        user.setEmail(request.email);
        user.setPassword(request.password);

        user.setDepartment(department);

        user.setEmailVerified(false);

        String code = String.valueOf(
                100000 + new Random().nextInt(900000)
        );

        user.setVerificationCode(code);

        userRepository.save(user);

        return code;
    }

    // LOGIN
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.password)) {
            return "WRONG_PASSWORD";
        }

        return "SUCCESS";
    }
}