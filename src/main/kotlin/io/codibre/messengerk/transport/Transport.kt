package io.codibre.messengerk.transport

import io.codibre.messengerk.Channel
import io.codibre.messengerk.ReceiverWorker

/**
 * Represents a transport used for sending and receiving envelopes over a specific channel.
 */
interface Transport : Sender, Receiver {
    /**
     * The name of the transport.
     */
    val name: String

    /**
     * Subscribes to the specified channel, returning a map of channels and associated receiver workers.
     *
     * @param channel The channel to subscribe to.
     * @return A map of channels and associated receiver workers.
     */
    fun subscribe(channel: Channel): Map<Channel, List<ReceiverWorker>>

    /**
     * Retrieves the list of receiver workers associated with the transport.
     *
     * @return The list of receiver workers.
     */
    fun getReceivers(): List<ReceiverWorker>
}
