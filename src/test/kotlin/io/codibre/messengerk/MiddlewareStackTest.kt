package io.codibre.messengerk

import io.codibre.messengerk.middleware.HelloWorldMiddleware
import io.codibre.messengerk.stamp.HelloStamp
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MiddlewareStackTest {

    @Test
    fun `it can handle an envelope with an empty middleware stack`() {
        val stack = MiddlewareStack()
        val envelope = Envelope<Any>("Foo message")
        val handledEnvelope = stack.current.handle(envelope, stack)

        assertTrue(handledEnvelope.hasNoStamps())
        assertTrue(handledEnvelope.message == "Foo message")
    }

    @Test
    fun `it creates middleware stack with middlewares`() {
        val helloMiddleware = HelloWorldMiddleware()
        val stack = MiddlewareStack(listOf(helloMiddleware))
        val envelope = Envelope<Any>("Foo message")
        assertTrue(stack.current is HelloWorldMiddleware)

        val handledEnvelope = stack.current.handle(envelope, stack)
        assertTrue(handledEnvelope.hasStamps())
        assertTrue(handledEnvelope.contains<HelloStamp>())
    }
}
