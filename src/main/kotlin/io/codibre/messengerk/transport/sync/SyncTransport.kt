package io.codibre.messengerk.transport.sync

import io.codibre.messengerk.MessageBusLocator
import io.codibre.messengerk.ReceiverWorker
import io.codibre.messengerk.stamp.ReceivedStamp
import io.codibre.messengerk.transport.Transport


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
    override fun subscribe(channel: io.codibre.messengerk.Channel): Map<io.codibre.messengerk.Channel, List<ReceiverWorker>> {
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
    override fun send(channel: io.codibre.messengerk.Channel, envelope: io.codibre.messengerk.Envelope<*>, key: String?): io.codibre.messengerk.Envelope<Any> =
        busLocator().dispatch(envelope, ReceivedStamp(transportName = name)).getOrThrow()

    /**
     * Receives messages from the specified channel.
     *
     * @param channel the channel to receive messages from
     * @return an empty list indicating no messages are received
     */
    override suspend fun receive(channel: io.codibre.messengerk.Channel): List<io.codibre.messengerk.Envelope<Any>> = emptyList()

    /**
     * Acknowledges the specified envelope.
     *
     * @param envelope the envelope to acknowledge
     * @return `true` indicating successful acknowledgement
     */
    override fun ack(envelope: io.codibre.messengerk.Envelope<*>): Boolean = true

    /**
     * Rejects the specified envelope.
     *
     * @param envelope the envelope to reject
     */
    override fun reject(envelope: io.codibre.messengerk.Envelope<Any>) {
        // Not implemented in this synchronous transport
    }
}
