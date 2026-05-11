package com.hakankuru.EventTime.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class ClubMemberId implements Serializable {
    
    private Long userId;
    private Long clubId;

    public ClubMemberId() {}

    public ClubMemberId(Long userId, Long clubId) {
        this.userId = userId;
        this.clubId = clubId;
    }
}