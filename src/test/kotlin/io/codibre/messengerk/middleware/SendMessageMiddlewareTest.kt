package io.codibre.messengerk.middleware

import io.codibre.messengerk.Channel
import io.codibre.messengerk.Envelope
import io.codibre.messengerk.MiddlewareStack
import io.codibre.messengerk.stamp.ReceivedStamp
import io.codibre.messengerk.stamp.SentStamp
import io.codibre.messengerk.transport.Route
import io.codibre.messengerk.transport.RouteRegistry
import io.codibre.messengerk.transport.memory.InMemoryTransport
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class SendMessageMiddlewareTest {

    class FooMessage

    @Test
    fun `it should send the message`() {
        @Suppress("UNCHECKED_CAST")
        val envelope = Envelope(FooMessage()) as Envelope<Any>
        val channel = Channel("fooChannel")
        val transport = InMemoryTransport(name = "fooInMemory")
        val routeRegistry = RouteRegistry()
        routeRegistry[envelope.message::class.qualifiedName.toString()] =
            { Route(channel, transport) }
        val middleware = SendMessageMiddleware(
            routeRegistry
        )
        val sentEnvelope = middleware.handle(envelope, MiddlewareStack())
        val stamp = sentEnvelope.firstOf<SentStamp>() as SentStamp

        assertTrue(stamp.transport == transport.name)
        assertTrue(envelope != sentEnvelope)
    }

    @Test
    fun `it should not send the message if it has a received stamp`() {
        val transport = InMemoryTransport(name = "fooInMemory")
        val channel = Channel("fooChannel")

        @Suppress("UNCHECKED_CAST")
        val envelope = Envelope(FooMessage())
            .with(ReceivedStamp(transport.name)) as Envelope<Any>

        val routeRegistry = RouteRegistry()
        routeRegistry[envelope.message::class.qualifiedName.toString()] =
            { Route(channel, transport) }

        val middleware = SendMessageMiddleware(
            routeRegistry,
        )
        val sentEnvelope = middleware.handle(envelope, MiddlewareStack())

        assertTrue(sentEnvelope.firstOf<SentStamp>() == null)
        assertTrue(envelope == sentEnvelope)
    }

    @Test
    fun `it should not send the message if there is no route available`() {
        val transport = InMemoryTransport(name = "fooInMemory")

        @Suppress("UNCHECKED_CAST")
        val envelope = Envelope(FooMessage())
            .with(ReceivedStamp(transport.name)) as Envelope<Any>
        val middleware = SendMessageMiddleware(
            RouteRegistry()
        )
        val sentEnvelope = middleware.handle(envelope, MiddlewareStack())

        assertTrue(sentEnvelope.firstOf<SentStamp>() == null)
        assertTrue(envelope == sentEnvelope)
    }
}
