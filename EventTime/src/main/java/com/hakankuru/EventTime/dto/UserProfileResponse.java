package com.hakankuru.EventTime.dto;

import com.hakankuru.EventTime.entity.GlobalRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String name;
    private String email;
    private GlobalRole globalRole;
    private List<UserClubDTO> clubs;
}
