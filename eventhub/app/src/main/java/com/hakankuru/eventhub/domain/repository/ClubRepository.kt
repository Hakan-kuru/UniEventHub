package com.hakankuru.eventhub.domain.repository

import com.hakankuru.eventhub.data.remote.response.ClubMemberResponse
import com.hakankuru.eventhub.domain.util.Result

interface ClubRepository {
    suspend fun getClubMembers(clubId: Long): Result<List<ClubMemberResponse>>
    suspend fun addMember(clubId: Long, userEmail: String, role: String = "MEMBER"): Result<Unit>
    suspend fun removeMember(clubId: Long, userId: Long): Result<Unit>
}
