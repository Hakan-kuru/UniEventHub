package com.hakankuru.eventhub.data.remote.response

data class UserProfileResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val globalRole: String?,
    val clubs: List<UserClubDTO>,
    /** ADMIN için hangi üniversiteyi yönettiği. SUPER_ADMIN ve USER için null. */
    val universityId: Long? = null
)

data class UserClubDTO(
    val clubId: Long,
    val clubName: String,
    val clubRole: String
)
