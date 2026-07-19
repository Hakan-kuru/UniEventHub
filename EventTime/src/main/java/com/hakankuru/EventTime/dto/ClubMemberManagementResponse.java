package com.hakankuru.EventTime.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClubMemberManagementResponse {
    private Long userId;
    private String name;
    private String email;
    private String departmentName;
    private String clubRole;
}
