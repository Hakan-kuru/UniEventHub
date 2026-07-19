package com.hakankuru.EventTime.controller;

import com.hakankuru.EventTime.dto.AuthResponse;
import com.hakankuru.EventTime.dto.LoginRequest;
import com.hakankuru.EventTime.dto.RegisterRequest;
import com.hakankuru.EventTime.entity.Departments;
import com.hakankuru.EventTime.entity.User;
import com.hakankuru.EventTime.repository.DepartmentRepository;
import com.hakankuru.EventTime.repository.UserRepository;
import com.hakankuru.EventTime.security.CustomUserDetails;
import com.hakankuru.EventTime.security.JwtService;
import com.hakankuru.EventTime.entity.GlobalRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin // Android bağlantısı için
public class AuthController {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDepartments(department);
        user.setGlobalRole(GlobalRole.USER); // Default role

        userRepository.save(user);

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(customUserDetails);

        return new AuthResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                jwtToken,
                user.getGlobalRole().name()   // ← role eklendi
        );
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request
    ) {
        // Authenticate the user (this checks email and password securely using our configuration)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If we reach here, authentication is successful
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(customUserDetails);

        return new AuthResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                jwtToken,
                user.getGlobalRole().name()   // ← role eklendi
        );
    }
}