package io.codibre.messengerk.serializer

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class JacksonSerializer {

    private val serializer: ObjectMapper =
        jacksonObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)

    fun encode(envelope: io.codibre.messengerk.Envelope<*>): String {
        return serializer.writeValueAsString(envelope)
    }

    fun decode(encoded: String): io.codibre.messengerk.Envelope<Any> {
        return serializer.readValue(
            encoded,
            object : TypeReference<io.codibre.messengerk.Envelope<Any>>() {})
    }
}
