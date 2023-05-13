package io.codibre.messengerk.handler

import io.codibre.messengerk.Envelope
import io.codibre.messengerk.annotations.MessageHandler
import io.codibre.messengerk.stamp.FooStamp
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class HandlerLocatorTest {

    class FooMessage

    class FooHandler {
        @MessageHandler
        fun handleFoo(envelope: Envelope<in FooMessage>): Envelope<in FooMessage> {
            return envelope.with(FooStamp())
        }
    }

    @Test
    fun `it can create a handler locator with no handlers`() {
        val handlerMap: MutableMap<String, MutableList<HandlerDescriptor>> = mutableMapOf()
        val handlerLocator = HandlerLocator(handlerMap)

        val envelope: Envelope<FooMessage> =
            Envelope(FooMessage())
        val handlers = handlerLocator.getHandlers(envelope)

        assertTrue(handlers.isEmpty())
    }

    @Test
    fun `it can create a handler locator with handlers`() {
        val envelope: Envelope<FooMessage> =
            Envelope(FooMessage())
        val handlersMap: MutableMap<String, MutableList<HandlerDescriptor>> =
            mutableMapOf(FooMessage::class.qualifiedName!! to mutableListOf(HandlerDescriptor.fromKFunction(FooHandler::handleFoo)))
        val handlerLocator = HandlerLocator(handlersMap)

        val handlers = handlerLocator.getHandlers(envelope)

        assertFalse(handlers.isEmpty())
    }
}
