package com.finalworksystem.infrastructure.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.finalworksystem.domain.user.model.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

data class AuthData(
    val token: String,
    val username: String
)

data class LogoutEvent(
    val userId: Int?,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: LogoutReason = LogoutReason.MANUAL
)

enum class LogoutReason {
    MANUAL,
    TOKEN_EXPIRED
}

class SessionManager(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")
        private val USER_KEY = stringPreferencesKey("user")
        private val USER_PROFILE_IMAGE_KEY = stringPreferencesKey("user_profile_image")
        private val AUTH_DATA_KEY = stringPreferencesKey("auth_data")
        private val LANGUAGE_KEY = stringPreferencesKey("app_language")
    }

    private val gson = Gson()

    private val _logoutEvents = MutableSharedFlow<LogoutEvent>()
    val logoutEvents: SharedFlow<LogoutEvent> = _logoutEvents.asSharedFlow()

    val userFlow: Flow<User?> = context.dataStore.data.map { preferences ->
        val userJson = preferences[USER_KEY]
        if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (_: Exception) {
                null
            }
        } else {
            null
        }
    }

    private val authDataFlow: Flow<AuthData?> = context.dataStore.data.map { preferences ->
        val authDataJson = preferences[AUTH_DATA_KEY]
        if (authDataJson != null) {
            try {
                gson.fromJson(authDataJson, AuthData::class.java)
            } catch (_: Exception) {
                null
            }
        } else {
            null
        }
    }

    val authTokenFlow: Flow<String?> = authDataFlow.map { authData ->
        authData?.token
    }

    val authUsernameFlow: Flow<String?> = authDataFlow.map { authData ->
        authData?.username
    }

    suspend fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        context.dataStore.edit { preferences ->
            preferences[USER_KEY] = userJson
        }
    }

    suspend fun saveAuthData(authData: AuthData) {
        val authDataJson = gson.toJson(authData)
        context.dataStore.edit { preferences ->
            preferences[AUTH_DATA_KEY] = authDataJson
        }
    }

    suspend fun clearSession(reason: LogoutReason = LogoutReason.MANUAL) {
        val currentUser = userFlow.first()
        val userId = currentUser?.id

        context.dataStore.edit { preferences ->
            preferences.remove(USER_KEY)
            preferences.remove(USER_PROFILE_IMAGE_KEY)
            preferences.remove(AUTH_DATA_KEY)
        }

        _logoutEvents.emit(LogoutEvent(userId = userId, reason = reason))
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[USER_KEY] != null
    }

    suspend fun writeData(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    fun readData(key: String): Flow<String?> = context.dataStore.data.map { preferences ->
        val prefKey = stringPreferencesKey(key)
        preferences[prefKey]
    }

    suspend fun removeData(key: String) {
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences.remove(prefKey)
        }
    }

    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    val languageFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY]
    }

    suspend fun saveLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }
}
