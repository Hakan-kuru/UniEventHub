package com.hakankuru.eventhub.data.remote.response

/**
 * Backend /api/v1/admin/users endpoint'inden dönen kullanıcı modeli.
 * universityId: ADMIN ise bağlı üniversite, USER/SUPER_ADMIN için null.
 */
data class AdminUserDto(
    val userId: Long,
    val name: String,
    val email: String,
    val role: String,          // "USER" | "ADMIN" | "SUPER_ADMIN"
    val universityId: Long?    // null → USER veya SUPER_ADMIN
)
