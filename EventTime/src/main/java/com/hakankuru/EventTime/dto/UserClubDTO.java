package com.hakankuru.EventTime.dto;

import com.hakankuru.EventTime.entity.ClubRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserClubDTO {
    private Long clubId;
    private String clubName;
    private ClubRole clubRole;
}
