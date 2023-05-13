package io.codibre.messengerk.transport

/**
 * Represents a route for message delivery, associating a channel with a transport.
 *
 * @property channel the channel through which messages are delivered
 * @property transport the transport responsible for delivering messages
 */
data class Route(
    val channel: io.codibre.messengerk.Channel,
    val transport: Transport
)
