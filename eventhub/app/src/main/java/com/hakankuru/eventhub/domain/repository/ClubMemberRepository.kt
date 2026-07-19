package com.hakankuru.eventhub.domain.repository

import com.hakankuru.eventhub.data.remote.response.ClubMemberManagementResponse
import com.hakankuru.eventhub.data.remote.response.UserSearchResponse
import com.hakankuru.eventhub.domain.util.Result

interface ClubMemberRepository {
    suspend fun getClubMembers(clubId: Long): Result<List<ClubMemberManagementResponse>>
    suspend fun searchUsersForClub(clubId: Long, email: String): Result<List<UserSearchResponse>>
    suspend fun addMemberToClub(clubId: Long, email: String): Result<Unit>
    suspend fun removeMemberFromClub(clubId: Long, userId: Long): Result<Unit>
}