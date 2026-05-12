package com.hakankuru.EventTime.controller;

import com.hakankuru.EventTime.dto.UserProfileResponse;
import com.hakankuru.EventTime.security.CustomUserDetails;
import com.hakankuru.EventTime.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        UserProfileResponse profile = userService.getUserProfile(userDetails.getUser().getUserId());
        return ResponseEntity.ok(profile);
    }
}