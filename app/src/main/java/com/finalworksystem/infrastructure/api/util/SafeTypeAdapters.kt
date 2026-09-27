package com.finalworksystem.infrastructure.api.util

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class SafeIntAdapter : TypeAdapter<Int>() {
    override fun write(out: JsonWriter, value: Int?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value)
        }
    }

    override fun read(reader: JsonReader): Int {
        return when (reader.peek()) {
            JsonToken.NULL -> {
                reader.nextNull()
                0
            }
            JsonToken.NUMBER -> reader.nextInt()
            JsonToken.STRING -> {
                val str = reader.nextString()
                str.toIntOrNull() ?: 0
            }
            JsonToken.BOOLEAN -> {
                if (reader.nextBoolean()) 1 else 0
            }
            else -> {
                reader.skipValue()
                0
            }
        }
    }
}

class SafeLongAdapter : TypeAdapter<Long>() {
    override fun write(out: JsonWriter, value: Long?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value)
        }
    }

    override fun read(reader: JsonReader): Long {
        return when (reader.peek()) {
            JsonToken.NULL -> {
                reader.nextNull()
                0L
            }
            JsonToken.NUMBER -> reader.nextLong()
            JsonToken.STRING -> {
                val str = reader.nextString()
                str.toLongOrNull() ?: 0L
            }
            JsonToken.BOOLEAN -> {
                if (reader.nextBoolean()) 1L else 0L
            }
            else -> {
                reader.skipValue()
                0L
            }
        }
    }
}

class SafeDoubleAdapter : TypeAdapter<Double>() {
    override fun write(out: JsonWriter, value: Double?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value)
        }
    }

    override fun read(reader: JsonReader): Double {
        return when (reader.peek()) {
            JsonToken.NULL -> {
                reader.nextNull()
                0.0
            }
            JsonToken.NUMBER -> reader.nextDouble()
            JsonToken.STRING -> {
                val str = reader.nextString()
                str.toDoubleOrNull() ?: 0.0
            }
            else -> {
                reader.skipValue()
                0.0
            }
        }
    }
}
