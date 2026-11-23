package com.finalworksystem.infrastructure.api.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull

/**
 * Lenient number serializers for API DTOs, applied per file with
 * `@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)`.
 *
 * - numbers sent as strings ("123") are parsed;
 * - invalid values ("AB", objects, arrays) fall back to 0;
 * - booleans map to 1/0;
 * - JSON `null` becomes 0 for non-null properties. For nullable properties (`Int?`)
 *   the generated nullable wrapper handles `null` itself, so it stays `null`.
 */
object SafeIntSerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.finalworksystem.SafeInt", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Int) = encoder.encodeInt(value)

    override fun deserialize(decoder: Decoder): Int {
        val primitive = decoder.decodeLenientPrimitive() ?: return 0
        val content = primitive.content
        return content.toIntOrNull()
            ?: content.toDoubleOrNull()?.toInt()
            ?: primitive.booleanOrNull?.let { if (it) 1 else 0 }
            ?: 0
    }
}

object SafeLongSerializer : KSerializer<Long> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.finalworksystem.SafeLong", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Long) = encoder.encodeLong(value)

    override fun deserialize(decoder: Decoder): Long {
        val primitive = decoder.decodeLenientPrimitive() ?: return 0L
        val content = primitive.content
        return content.toLongOrNull()
            ?: content.toDoubleOrNull()?.toLong()
            ?: primitive.booleanOrNull?.let { if (it) 1L else 0L }
            ?: 0L
    }
}

object SafeDoubleSerializer : KSerializer<Double> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.finalworksystem.SafeDouble", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: Double) = encoder.encodeDouble(value)

    override fun deserialize(decoder: Decoder): Double {
        val primitive = decoder.decodeLenientPrimitive() ?: return 0.0
        return primitive.content.toDoubleOrNull() ?: 0.0
    }
}

/** Returns the current value as a primitive, or null for JSON null / objects / arrays. */
private fun Decoder.decodeLenientPrimitive(): JsonPrimitive? {
    if (this !is JsonDecoder) {
        return JsonPrimitive(decodeString())
    }
    val element = decodeJsonElement()
    return if (element is JsonPrimitive && element !is JsonNull) element else null
}
