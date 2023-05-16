package com.github.Codibre.messengerk.serializer

import com.github.Codibre.messengerk.Envelope
import com.github.Codibre.messengerk.stamp.BusNameStamp
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
