package com.hakankuru.EventTime.controller;

import com.hakankuru.EventTime.dto.AuthResponse;
import com.hakankuru.EventTime.dto.LoginRequest;
import com.hakankuru.EventTime.dto.RegisterRequest;
import com.hakankuru.EventTime.entity.Departments;
import com.hakankuru.EventTime.entity.User;
import com.hakankuru.EventTime.repository.DepartmentRepository;
import com.hakankuru.EventTime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin // Android bağlantısı için
public class AuthController {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @PostMapping("/register")
    public AuthResponse register(
            @RequestBody RegisterRequest request
    ) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Departments department = departmentRepository.findById(
                request.getDepartmentId()
        ).orElseThrow(() -> new RuntimeException("Department not found"));

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setDepartments(department);

        userRepository.save(user);

        return new AuthResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request
    ) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        return new AuthResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail()
        );
    }
}