package com.github.codibre.messengerk.core.serializer

import com.github.codibre.messengerk.core.Envelope
import com.github.codibre.messengerk.core.stamp.BusNameStamp
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class JacksonSerializerTest {

    class FooMessage(val name: String = "ola")

    private val serializer = JacksonSerializer()

    @Test
    fun `it should decode to the same type after serialization`() {
        val envelope = Envelope(FooMessage()).with(BusNameStamp("Busao"))
        val serialized = serializer.encode(envelope)
        val decoded = serializer.decode(serialized)
        assertTrue(decoded.contains<BusNameStamp>())
    }
}
