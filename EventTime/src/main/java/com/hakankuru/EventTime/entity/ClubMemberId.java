package com.hakankuru.EventTime.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClubMemberId that = (ClubMemberId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(clubId, that.clubId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, clubId);
    }
}