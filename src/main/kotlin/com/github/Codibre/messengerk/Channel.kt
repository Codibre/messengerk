package com.github.Codibre.messengerk

/**
 * Represents a communication channel for message exchange.
 *
 * @param name The name of the channel.
 * @param messageTypes The list of message types supported by the channel.
 * @param receiverConfig The configuration for message receivers on this channel.
 * @param senderConfig The configuration for message senders on this channel.
 */
class Channel(
    val name: String,
    val messageTypes: List<String> = emptyList(),
    val receiverConfig: ReceiverConfig = ReceiverConfig(),
    val senderConfig: SenderConfig = SenderConfig()
)

/**
 * Configuration for message receivers on a channel.
 *
 * @param concurrency The maximum number of concurrent receivers for the channel.
 */
data class ReceiverConfig(
    val concurrency: Int = 1
)

/**
 * Configuration for message senders on a channel.
 *
 * @param acks The number of acknowledgments expected for sent messages.
 */
data class SenderConfig(
    val acks: Int = 0
)
