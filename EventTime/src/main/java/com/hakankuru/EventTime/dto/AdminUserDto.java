package com.hakankuru.EventTime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDto {
    private Long userId;
    private String name;
    private String email;
    private String role;         // USER / ADMIN / SUPER_ADMIN
    private Long universityId;   // null ise USER veya SUPER_ADMIN
}
