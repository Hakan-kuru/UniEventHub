package com.hakankuru.EventTime.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "club_members")
public class ClubMember {

    @EmbeddedId
    private ClubMemberId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("clubId")
    @JoinColumn(name = "club_id")
    private Club club;

    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
