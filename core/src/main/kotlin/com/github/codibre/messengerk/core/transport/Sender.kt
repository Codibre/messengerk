package com.github.codibre.messengerk.core.transport

import com.github.codibre.messengerk.core.Channel
import com.github.codibre.messengerk.core.Envelope

/**
 * Represents a sender responsible for sending envelopes over a specific channel.
 */
interface Sender {
    /**
     * Sends the given envelope over the specified channel.
     *
     * @param channel The channel to send the envelope on.
     * @param envelope The envelope to send.
     * @param key The optional key associated with the message.
     * @return The sent envelope.
     */
    fun send(
        channel: Channel,
        envelope: Envelope<*>,
        key: String? = null
    ): Envelope<*>
}
