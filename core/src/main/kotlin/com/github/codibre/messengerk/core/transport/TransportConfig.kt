package com.github.codibre.messengerk.core.transport

/**
 * Configuration options for a transport.
 *
 * @property options The general configuration options for the transport.
 * @property senderConfig The configuration options specific to the sender functionality of the transport.
 * @property receiverConfig The configuration options specific to the receiver functionality of the transport.
 */
data class TransportConfig(
    val options: Map<String, String> = emptyMap(),
    val senderConfig: Map<String, String> = emptyMap(),
    val receiverConfig: Map<String, String> = emptyMap()
)
