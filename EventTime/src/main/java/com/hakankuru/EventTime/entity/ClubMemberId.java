package com.hakankuru.EventTime.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ClubMemberId implements Serializable {
    private Long userId;
    private Long clubId;
}
