package io.codibre.messengerk.transport

import io.kotest.common.runBlocking
import io.codibre.messengerk.Channel
import io.codibre.messengerk.Envelope
import io.codibre.messengerk.stamp.FooStamp
import io.codibre.messengerk.stamp.JohnStamp
import io.codibre.messengerk.stamp.TransportMessageIdStamp
import io.codibre.messengerk.transport.memory.InMemoryTransport
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class InMemoryTransportTest {

    private lateinit var transport: InMemoryTransport
    private lateinit var channel: Channel

    data class FooMessage(val name: String = "Foo")
    data class JohnMessage(val name: String = "Foo")

    @BeforeEach
    fun receive() {
        channel = Channel("fooChannel")
        transport = InMemoryTransport(name = "fooTransport")
    }

    @Test
    fun send() {
        val envelope = Envelope(FooMessage()).with(FooStamp())
        val envelope2 = Envelope(JohnMessage()).with(JohnStamp())
        transport = InMemoryTransport(name = "fooTransport")
        //todo: implement behavior with no transport
        var envelopes: List<Envelope<Any>> = listOf()

        transport.send(Channel("fooChannel"), envelope)
        transport.send(Channel("fooChannel"), envelope2)

        runBlocking {
            envelopes = transport.receive(channel)
        }

        assertTrue(envelopes.count() == 2)
        assertTrue(envelopes[0].message is FooMessage)
        assertTrue(envelopes[0].contains<FooStamp>())
        assertTrue(envelopes[1].message is JohnMessage)
        assertTrue(envelopes[1].contains<JohnStamp>())
    }

    @Test
    fun ack() {
        val envelope = Envelope(FooMessage()).with(FooStamp())
        val sentEnvelope = transport.send(Channel("fooChannel"), envelope)
        transport.ack(sentEnvelope)
        runBlocking {
            assertTrue(transport.receive(channel).isEmpty())
        }
        assertTrue(transport.acknowledged()[0] == sentEnvelope)
    }

    @Test
    fun reject() {
        val envelope = Envelope(FooMessage()).with(FooStamp())
        transport.send(Channel("fooChannel"), envelope)
        runBlocking {
            val receivedEnvelope = transport.receive(channel)[0]
            transport.reject(receivedEnvelope)
            assertTrue(transport.receive(channel).isEmpty())
            assertTrue(transport.rejected()[0] == receivedEnvelope)
        }
    }

    @Test
    fun sent() {
        val envelope = Envelope(FooMessage()).with(FooStamp())
        transport.send(Channel("fooChannel"), envelope)
        val sent = transport.sent()[0]
        assertTrue(sent == envelope.with(TransportMessageIdStamp(1)))
    }

    @Test
    fun getReceivers() {
        assertTrue(transport.getReceivers().isEmpty())
    }
}
