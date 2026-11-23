package com.finalworksystem.infrastructure.session

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.infrastructure.api.serialization.ApiJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import timber.log.Timber
import java.io.File
import java.io.IOException

/**
 * Moves the session of previous app versions (JSON strings in the preferences DataStore
 * `session_prefs`) into the typed [Session] document and deletes the old file,
 * together with the profile images that were stored there.
 * Users stay signed in after the update.
 */
class LegacySessionPreferencesMigration(
    private val legacyFile: File
) : DataMigration<Session> {

    override suspend fun shouldMigrate(currentData: Session): Boolean = legacyFile.exists()

    override suspend fun migrate(currentData: Session): Session {
        val preferences = readLegacyPreferences() ?: return currentData

        return currentData.copy(
            user = currentData.user ?: preferences.decode(USER_KEY, User.serializer()),
            auth = currentData.auth ?: preferences.decode(AUTH_DATA_KEY, AuthData.serializer()),
            legacyLanguage = currentData.legacyLanguage ?: preferences[stringPreferencesKey(LANGUAGE_KEY)]
        )
    }

    override suspend fun cleanUp() {
        if (legacyFile.exists() && !legacyFile.delete()) {
            Timber.w("Cannot delete legacy session file")
        }
    }

    private suspend fun readLegacyPreferences(): Preferences? {
        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.IO + job)

        return try {
            PreferenceDataStoreFactory.create(scope = scope, produceFile = { legacyFile }).data.first()
        } catch (e: IOException) {
            Timber.w(e, "Cannot read legacy session")
            null
        } finally {
            // Wait until the temporary DataStore has released the file.
            withContext(NonCancellable) { job.cancelAndJoin() }
        }
    }

    private fun <T> Preferences.decode(key: String, serializer: KSerializer<T>): T? {
        val json = this[stringPreferencesKey(key)] ?: return null

        return runCatching { ApiJson.decodeFromString(serializer, json) }
            .onFailure { Timber.w(it, "Cannot migrate legacy session value %s", key) }
            .getOrNull()
    }

    companion object {
        const val LEGACY_FILE_NAME = "session_prefs.preferences_pb"
        private const val USER_KEY = "user"
        private const val AUTH_DATA_KEY = "auth_data"
        private const val LANGUAGE_KEY = "app_language"
    }
}
