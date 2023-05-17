package com.github.codibre.messengerk.core.handler

import com.github.codibre.messengerk.core.Envelope
import com.github.codibre.messengerk.core.annotations.MessageHandler
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class HandlerDescriptorTest {

    data class FooMessage(val name: String)

    class FooHandler {
        @MessageHandler
        fun handleFoo(envelope: Envelope<in FooMessage>): Envelope<in FooMessage> {
            print("Handling FooMessage")
            return envelope
        }
    }

    @Test
    fun `it should handle a message through a handlerDescriptor callable`() {
        val buffer = ByteArrayOutputStream()
        System.setOut(PrintStream(buffer))
        HandlerDescriptor.fromKFunction(FooHandler::handleFoo).callable.invoke(
            Envelope(
                FooMessage(
                    "foo"
                )
            )
        )
        assertTrue(buffer.toString() == "Handling FooMessage")
    }
}
