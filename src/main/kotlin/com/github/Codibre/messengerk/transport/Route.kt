package com.github.Codibre.messengerk.transport

import com.github.Codibre.messengerk.Channel

/**
 * Represents a route for message delivery, associating a channel with a transport.
 *
 * @property channel the channel through which messages are delivered
 * @property transport the transport responsible for delivering messages
 */
data class Route(
    val channel: Channel,
    val transport: Transport
)
