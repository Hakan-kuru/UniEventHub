package com.hakankuru.EventTime.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "club_members")
@Getter
@Setter
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

    @Enumerated(EnumType.STRING)
    private ClubRole clubRole;

    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
