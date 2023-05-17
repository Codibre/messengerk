package com.github.codibre.messengerk.core.handler

import com.github.codibre.messengerk.core.Envelope
import com.github.codibre.messengerk.core.annotations.MessageHandler
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream


internal class MessageHandlerTest {

    class FooMessage

    class FooHandler {
        @MessageHandler
        fun handle(envelope: Envelope<in FooMessage>): Envelope<in FooMessage> {
            print("Handling FooMessage")
            return envelope
        }
    }

    @Test
    fun `it should handle an envelope`() {
        val envelope = Envelope(FooMessage())
        val buffer = ByteArrayOutputStream()
        System.setOut(PrintStream(buffer))
        FooHandler().handle(envelope)
        assertTrue(buffer.toString() == "Handling FooMessage")
    }
}
