package com.hakankuru.eventhub.data.remote.response

data class ClubMemberManagementResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val departmentName: String?,
    val clubRole: String
)