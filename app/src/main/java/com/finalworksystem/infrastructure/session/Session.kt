package com.finalworksystem.infrastructure.session

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.infrastructure.api.serialization.ApiJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class AuthData(
    val token: String,
    val username: String
)

/**
 * Everything the app persists about the signed-in user, stored as one typed
 * DataStore document (`files/datastore/session.json`).
 */
@Serializable
data class Session(
    val user: User? = null,
    val auth: AuthData? = null,
    /** Language selected in an app version before the per-app language API; applied once. */
    val legacyLanguage: String? = null
)

object SessionSerializer : Serializer<Session> {
    override val defaultValue: Session = Session()

    override suspend fun readFrom(input: InputStream): Session {
        try {
            return ApiJson.decodeFromString(Session.serializer(), input.readBytes().decodeToString())
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read session", e)
        } catch (e: IllegalArgumentException) {
            throw CorruptionException("Cannot read session", e)
        }
    }

    override suspend fun writeTo(t: Session, output: OutputStream) {
        output.write(ApiJson.encodeToString(Session.serializer(), t).encodeToByteArray())
    }
}
