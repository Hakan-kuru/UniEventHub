package com.hakankuru.EventTime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubCreateRequest {
    private String name;
    private String description;
}
