package com.finalworksystem.infrastructure.api.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * Shared JSON configuration for the API and local storage.
 *
 * Tolerant like the previous Gson setup:
 * - unknown fields are ignored;
 * - `null` or a missing value for a non-null property with a default uses the default;
 * - `null` properties are not written into request bodies.
 */
@OptIn(ExperimentalSerializationApi::class)
val ApiJson: Json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    explicitNulls = false
    encodeDefaults = true
    isLenient = true
}

/**
 * Unwraps an object that the API sometimes sends inside a wrapper,
 * e.g. `{"result": {...}}` or `{"work": {...}}`. The first key that holds
 * an object wins; otherwise the element is used as is.
 */
open class UnwrappingSerializer<T : Any>(
    serializer: KSerializer<T>,
    private val wrapperKeys: List<String>
) : JsonTransformingSerializer<T>(serializer) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element !is JsonObject) return element
        for (key in wrapperKeys) {
            val inner = element[key]
            if (inner is JsonObject) return inner
        }
        return element
    }
}
