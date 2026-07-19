package com.hakankuru.eventhub.domain.model

import com.hakankuru.eventhub.data.remote.response.UserProfileResponse

/**
 * Merkezi rol/yetki kontrol sistemi.
 * UI içinde string karşılaştırması yapılmaz; tüm yetki kararları buradan geçer.
 */
object RoleGuard {

    fun isSuperAdmin(profile: UserProfileResponse?): Boolean =
        profile?.globalRole == "SUPER_ADMIN"

    fun isClubAdmin(profile: UserProfileResponse?, clubId: Long): Boolean =
        profile?.clubs?.any { it.clubId == clubId && it.clubRole == "ADMIN" } == true

    fun isClubMember(profile: UserProfileResponse?, clubId: Long): Boolean =
        profile?.clubs?.any { it.clubId == clubId } == true

    /** Kullanıcının admin olduğu tüm kulüp id'lerini döner. */
    fun adminClubIds(profile: UserProfileResponse?): Set<Long> =
        profile?.clubs?.filter { it.clubRole == "ADMIN" }?.map { it.clubId }?.toSet() ?: emptySet()

    /** Kullanıcının üye olduğu tüm kulüp id'lerini döner. */
    fun memberClubIds(profile: UserProfileResponse?): Set<Long> =
        profile?.clubs?.map { it.clubId }?.toSet() ?: emptySet()
}
