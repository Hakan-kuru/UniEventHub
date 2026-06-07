package com.hakankuru.eventhub.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Context'e DataStore özelliği ekliyoruz
private val Context.dataStore by preferencesDataStore(name = "session")

class SessionManager(
    private val context: Context
) {

    companion object {

        // Kullanıcı giriş yaptı mı bilgisini tutacağız
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        // JWT Token bilgisini tutacağız
        private val JWT_TOKEN = stringPreferencesKey("jwt_token")
        // Kullanıcının global rolünü tutacağız (SUPER_ADMIN, ADMIN, USER vs.)
        private val USER_ROLE  = stringPreferencesKey("user_role")
    }

    // Login olunca true, token ve role kaydedilecek
    suspend fun saveAuthSession(isLoggedIn: Boolean, token: String? = null, role: String? = null) {

        context.dataStore.edit { preferences ->

            preferences[IS_LOGGED_IN] = isLoggedIn
            if (token != null) {
                preferences[JWT_TOKEN] = token
            } else {
                preferences.remove(JWT_TOKEN)
            }
            if (role != null) {
                preferences[USER_ROLE] = role
            } else {
                preferences.remove(USER_ROLE)
            }
        }
    }

    // Kaydedilmiş role'ü okuyacağız
    fun getUserRole(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ROLE]
        }
    }

    // Login durumunu okuyacağız
    fun getLoginState(): Flow<Boolean> {

        return context.dataStore.data.map { preferences ->

            preferences[IS_LOGGED_IN] ?: false
        }
    }

    // JWT Token okuyacağız
    fun getAuthToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[JWT_TOKEN]
        }
    }

    // Logout için session temizliği
    suspend fun clearSession() {

        context.dataStore.edit { preferences ->

            preferences.clear()
        }
    }
}