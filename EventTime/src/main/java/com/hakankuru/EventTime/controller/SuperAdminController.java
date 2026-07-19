package com.hakankuru.EventTime.controller;

import com.hakankuru.EventTime.dto.AdminResponse;
import com.hakankuru.EventTime.dto.AdminUserDto;
import com.hakankuru.EventTime.dto.AssignAdminRequest;
import com.hakankuru.EventTime.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    /** POST /api/v1/admin/assign - email ile ADMIN atar */
    @PostMapping("/assign")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AdminResponse> assignAdmin(@RequestBody AssignAdminRequest request) {
        return ResponseEntity.ok(superAdminService.assignAdminByEmail(request));
    }

    /** DELETE /api/v1/admin/{userId} - Admin rolunu kaldirir */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> removeAdmin(@PathVariable Long userId) {
        superAdminService.removeAdmin(userId);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/v1/admin/list - Tum adminleri listeler */
    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<AdminResponse>> getAllAdmins() {
        return ResponseEntity.ok(superAdminService.getAllAdmins());
    }

    /** GET /api/v1/admin/users - Sistemdeki tum kullanicilari listeler */
    @GetMapping("/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<AdminUserDto>> getAllUsers() {
        return ResponseEntity.ok(superAdminService.getAllUsers());
    }
}
