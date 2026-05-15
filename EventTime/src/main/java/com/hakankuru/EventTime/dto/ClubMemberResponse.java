package com.hakankuru.EventTime.dto;

import com.hakankuru.EventTime.entity.ClubRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubMemberResponse {
    private Long userId;
    private String email;
    private String name;
    private ClubRole role;
    private LocalDateTime startAt;
}
