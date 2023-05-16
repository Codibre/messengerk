package com.github.Codibre.messengerk.handler

import com.github.Codibre.messengerk.Envelope
import com.github.Codibre.messengerk.annotations.MessageHandler
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
