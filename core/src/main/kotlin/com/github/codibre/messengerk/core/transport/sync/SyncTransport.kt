package com.github.codibre.messengerk.core.transport.sync

import com.github.codibre.messengerk.core.Channel
import com.github.codibre.messengerk.core.Envelope
import com.github.codibre.messengerk.core.MessageBusLocator
import com.github.codibre.messengerk.core.ReceiverWorker
import com.github.codibre.messengerk.core.stamp.ReceivedStamp
import com.github.codibre.messengerk.core.transport.Transport


/**
 * Synchronous implementation of the [Transport] interface.
 *
 * @param name the name of the transport
 * @param broker the broker type (default: "sync")
 * @param busLocator the message bus locator used for message dispatching
 */
class SyncTransport(
    override val name: String,
    val broker: String = "sync",
    val busLocator: MessageBusLocator
) : Transport {

    /**
     * Subscribes to the specified channel.
     *
     * @param channel the channel to subscribe to
     * @return an empty map indicating no receivers are registered
     */
    override fun subscribe(channel: Channel): Map<Channel, List<ReceiverWorker>> {
        return emptyMap()
    }

    /**
     * Retrieves the registered receivers.
     *
     * @return an empty list indicating no receivers are registered
     */
    override fun getReceivers(): List<ReceiverWorker> = emptyList()

    /**
     * Sends the specified envelope through the transport.
     *
     * @param channel the channel to send the envelope to
     * @param envelope the envelope to send
     * @param key the optional routing key (not used in this implementation)
     * @return the envelope after it has been dispatched by the message bus
     */
    override fun send(
        channel: Channel,
        envelope: Envelope<*>,
        key: String?
    ): Envelope<Any> =
        busLocator().dispatch(envelope, ReceivedStamp(transportName = name)).getOrThrow()

    /**
     * Receives messages from the specified channel.
     *
     * @param channel the channel to receive messages from
     * @return an empty list indicating no messages are received
     */
    override suspend fun receive(channel: Channel): List<Envelope<Any>> =
        emptyList()

    /**
     * Acknowledges the specified envelope.
     *
     * @param envelope the envelope to acknowledge
     * @return `true` indicating successful acknowledgement
     */
    override fun ack(envelope: Envelope<*>): Boolean = true

    /**
     * Rejects the specified envelope.
     *
     * @param envelope the envelope to reject
     */
    override fun reject(envelope: Envelope<Any>) {
        // Not implemented in this synchronous transport
    }
}
