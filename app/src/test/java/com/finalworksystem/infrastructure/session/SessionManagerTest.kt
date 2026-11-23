package com.finalworksystem.infrastructure.session

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.infrastructure.api.serialization.ApiJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SessionManagerTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val user = User(
        id = 7,
        username = "jnovak",
        firstname = "Jan",
        lastname = "Novak",
        fullName = "Jan Novak",
        email = "jan@example.com",
        token = "token",
        degreeBefore = null,
        degreeAfter = null,
        roles = listOf("ROLE_STUDENT")
    )

    private val sessionFile get() = File(temporaryFolder.root, "session.json")
    private val legacyFile get() = File(temporaryFolder.root, "session_prefs.preferences_pb")

    private fun sessionManager(scope: CoroutineScope): SessionManager {
        return SessionManager(SessionManager.createDataStore(sessionFile, legacyFile, scope))
    }

    @Test
    fun `stores user and credentials`() = runTest {
        val sessionManager = sessionManager(backgroundScope)

        sessionManager.saveAuthData(AuthData("token", "jnovak"))
        sessionManager.saveUser(user)

        assertEquals(AuthData("token", "jnovak"), sessionManager.getAuthData())
        assertEquals(user, sessionManager.userFlow.first())
        assertTrue(sessionManager.isLoggedInFlow.first())
    }

    @Test
    fun `logout clears the session and emits an event`() = runTest {
        val sessionManager = sessionManager(backgroundScope)
        sessionManager.saveAuthData(AuthData("token", "jnovak"))
        sessionManager.saveUser(user)

        sessionManager.logoutEvents.test {
            sessionManager.clearSession(LogoutReason.TOKEN_EXPIRED)

            val event = awaitItem()
            assertEquals(7, event.userId)
            assertEquals(LogoutReason.TOKEN_EXPIRED, event.reason)
        }

        assertNull(sessionManager.getAuthData())
        assertFalse(sessionManager.isLoggedInFlow.first())
    }

    @Test
    fun `login state changes are observable`() = runTest {
        val sessionManager = sessionManager(backgroundScope)

        sessionManager.isLoggedInFlow.test {
            assertFalse(awaitItem())

            sessionManager.saveUser(user)
            assertTrue(awaitItem())

            sessionManager.clearSession()
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `session of the previous app version is migrated`() = runTest {
        writeLegacyPreferences(
            "user" to ApiJson.encodeToString(User.serializer(), user),
            "auth_data" to """{"token":"legacy-token","username":"jnovak"}""",
            "app_language" to "ru",
            "profile_image_7" to "base64-image"
        )

        val sessionManager = sessionManager(backgroundScope)

        assertEquals(user, sessionManager.userFlow.first())
        assertEquals(AuthData("legacy-token", "jnovak"), sessionManager.getAuthData())
        assertFalse("Legacy file with cached images is deleted", legacyFile.exists())

        assertEquals("ru", sessionManager.consumeLegacyLanguage())
        assertNull("Language is applied only once", sessionManager.consumeLegacyLanguage())
    }

    @Test
    fun `corrupted session file starts an empty session`() = runTest {
        sessionFile.writeText("{ not json")

        val sessionManager = sessionManager(backgroundScope)

        assertNull(sessionManager.userFlow.first())
        assertNull(sessionManager.getAuthData())
    }

    private suspend fun writeLegacyPreferences(vararg values: Pair<String, String>) {
        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.IO + job)
        try {
            val dataStore = PreferenceDataStoreFactory.create(scope = scope) { legacyFile }
            dataStore.edit { preferences ->
                values.forEach { (key, value) -> preferences[stringPreferencesKey(key)] = value }
            }
        } finally {
            job.cancelAndJoin()
        }
    }
}
