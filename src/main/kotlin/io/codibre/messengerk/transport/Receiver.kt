package io.codibre.messengerk.transport

import io.codibre.messengerk.Channel
import io.codibre.messengerk.Envelope

/**
 * Represents a receiver that handles message receiving, acknowledgment, and rejection.
 */
interface Receiver {
    /**
     * Receives messages from the specified channel.
     *
     * @param channel the channel from which to receive messages
     * @return a list of received envelopes
     */
    suspend fun receive(channel: Channel): List<Envelope<Any>>

    /**
     * Acknowledges the specified envelope.
     *
     * @param envelope the envelope to acknowledge
     * @return `true` if the acknowledgment is successful, `false` otherwise
     */
    fun ack(envelope: Envelope<*>): Boolean

    /**
     * Rejects the specified envelope.
     *
     * @param envelope the envelope to reject
     */
    fun reject(envelope: Envelope<Any>)
}
