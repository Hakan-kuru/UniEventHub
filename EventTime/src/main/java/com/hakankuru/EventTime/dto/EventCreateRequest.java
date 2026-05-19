package com.hakankuru.EventTime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {
    private Long clubId;
    private String title;
    private String description;
}
