package com.github.Codibre.messengerk

import com.github.Codibre.messengerk.middleware.HelloWorldMiddleware
import com.github.Codibre.messengerk.stamp.FooStamp
import com.github.Codibre.messengerk.stamp.HelloStamp
import com.github.Codibre.messengerk.stamp.JohnStamp
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class DefaultMessageBusTest {

    @Test
    fun `it can dispatch a message without stamps`() {
        val messageBus = DefaultMessageBus()
        val envelope = messageBus.dispatch("Foo Message").getOrThrow()

        assertTrue(envelope.hasNoStamps())
    }

    @Test
    fun `it can dispatch a message with stamps`() {
        val messageBus = DefaultMessageBus()
        val envelope = messageBus.dispatch("Foo Message", FooStamp(), JohnStamp()).getOrThrow()
        assertTrue(envelope.hasStamps())
        assertTrue(envelope.contains<FooStamp>())
        assertTrue(envelope.contains<JohnStamp>())
    }

    @Test
    fun `it can inject send middlewares`() {
        val middlewares = listOf(HelloWorldMiddleware())
        val messageBus = DefaultMessageBus(middlewares = middlewares)
        val envelope = messageBus.dispatch("Foo Message", FooStamp(), JohnStamp()).getOrThrow()

        assertTrue(envelope.hasStamps())
        assertTrue(envelope.contains<FooStamp>())
        assertTrue(envelope.contains<JohnStamp>())
        assertTrue(envelope.contains<HelloStamp>())
    }
}
