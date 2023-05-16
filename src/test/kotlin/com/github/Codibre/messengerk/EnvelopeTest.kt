package com.github.Codibre.messengerk

import com.github.Codibre.messengerk.stamp.FooStamp
import com.github.Codibre.messengerk.stamp.JohnStamp
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class EnvelopeTest {

    @Test
    fun `it can create an envelope without stamps`() {
        val message = "Foo Message"
        val envelope = Envelope(message)

        assertTrue(envelope.hasNoStamps())
        assertTrue(envelope.notContains<FooStamp>())
        assertTrue(envelope.notContains<JohnStamp>())
    }

    @Test
    fun `it can create an envelope with stamps`() {
        val message = "Foo Message"
        val envelope = Envelope(
            message, StampCollection.buildFromList(
                listOf(
                    FooStamp(),
                    JohnStamp()
                )
            )
        )

        assertTrue(envelope.hasStamps())
        assertTrue(envelope.contains<FooStamp>())
        assertTrue(envelope.contains<JohnStamp>())
    }

    @Test
    fun `it can add stamps after creation`() {
        val message = "Foo Message"
        val envelope = Envelope(message, StampCollection())
        val stampedEnvelope = envelope.with(FooStamp())

        assertTrue(stampedEnvelope.contains<FooStamp>())
    }
}
