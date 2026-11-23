package com.finalworksystem.infrastructure.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.finalworksystem.domain.user.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.File

data class LogoutEvent(
    val userId: Int?,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: LogoutReason = LogoutReason.MANUAL
)

enum class LogoutReason {
    MANUAL,
    TOKEN_EXPIRED
}

class SessionManager(private val dataStore: DataStore<Session>) {

    private val _logoutEvents = MutableSharedFlow<LogoutEvent>()
    val logoutEvents: SharedFlow<LogoutEvent> = _logoutEvents.asSharedFlow()

    val userFlow: Flow<User?> = dataStore.data.map { it.user }.distinctUntilChanged()

    val authDataFlow: Flow<AuthData?> = dataStore.data.map { it.auth }.distinctUntilChanged()

    val isLoggedInFlow: Flow<Boolean> = dataStore.data.map { it.user != null }.distinctUntilChanged()

    suspend fun getAuthData(): AuthData? = authDataFlow.first()

    suspend fun saveUser(user: User) {
        dataStore.updateData { it.copy(user = user) }
    }

    suspend fun saveAuthData(authData: AuthData) {
        dataStore.updateData { it.copy(auth = authData) }
    }

    /**
     * Removes only the stored credentials without emitting a logout event.
     * Used to roll back a partially completed login.
     */
    suspend fun clearAuthData() {
        dataStore.updateData { it.copy(auth = null) }
    }

    suspend fun clearSession(reason: LogoutReason = LogoutReason.MANUAL) {
        var userId: Int? = null
        dataStore.updateData { session ->
            userId = session.user?.id
            session.copy(user = null, auth = null)
        }

        _logoutEvents.emit(LogoutEvent(userId = userId, reason = reason))
    }

    /** Returns the language chosen in an old app version once and forgets it. */
    suspend fun consumeLegacyLanguage(): String? {
        var language: String? = null
        dataStore.updateData { session ->
            language = session.legacyLanguage
            if (session.legacyLanguage == null) session else session.copy(legacyLanguage = null)
        }

        return language
    }

    companion object {
        private const val FILE_NAME = "session.json"

        fun createDataStore(context: Context): DataStore<Session> {
            return createDataStore(
                file = context.dataStoreFile(FILE_NAME),
                legacyFile = context.dataStoreFile(LegacySessionPreferencesMigration.LEGACY_FILE_NAME)
            )
        }

        fun createDataStore(file: File, legacyFile: File, scope: CoroutineScope? = null): DataStore<Session> {
            val corruptionHandler = ReplaceFileCorruptionHandler { e ->
                Timber.w(e, "Session file is corrupted, starting with an empty session")
                SessionSerializer.defaultValue
            }
            val migrations = listOf(LegacySessionPreferencesMigration(legacyFile))

            return if (scope != null) {
                DataStoreFactory.create(SessionSerializer, corruptionHandler, migrations, scope) { file }
            } else {
                DataStoreFactory.create(SessionSerializer, corruptionHandler, migrations) { file }
            }
        }
    }
}
