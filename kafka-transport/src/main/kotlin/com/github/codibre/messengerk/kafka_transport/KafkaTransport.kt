package com.github.codibre.messengerk.kafka_transport

import com.github.codibre.messengerk.core.Channel
import com.github.codibre.messengerk.core.Envelope
import com.github.codibre.messengerk.core.serializer.JacksonSerializer
import com.github.codibre.messengerk.core.transport.Transport
import com.github.codibre.messengerk.core.transport.TransportConfig
import com.github.codibre.messengerk.core.util.StringUtil
import java.util.*

/**
 * Kafka implementation of the Transport interface for sending and receiving messages using Kafka.
 *
 * @param name The name of the Kafka transport.
 * @param transportConfig The transport configuration.
 * @param broker The Kafka broker identifier.
 * @param serializer The serializer used to serialize/deserialize message envelopes.
 */
class KafkaTransport(
    override val name: String,
    private val transportConfig: TransportConfig,
    val broker: String = "kafka",
    private val serializer: JacksonSerializer = JacksonSerializer()
) : Transport {

    private var receivers: MutableMap<Channel, List<KafkaReceiver>> = mutableMapOf()
    private lateinit var sender: KafkaSender

    /**
     * Lazily initializes and returns the KafkaSender instance.
     *
     * @return The KafkaSender instance.
     */
    private fun getSender(): KafkaSender {
        if (!this::sender.isInitialized) {
            val props = setProperties(Properties(), transportConfig.options)
            sender = KafkaSender(props, serializer)
        }

        return sender
    }

    override suspend fun receive(channel: Channel): List<Envelope<Any>> {
        // TODO: Implement Kafka message receiving logic
        return listOf()
    }

    /**
     * Sets the properties based on the provided values.
     *
     * @param props The Kafka producer/consumer properties.
     * @param values The map of property keys and values.
     * @return The updated properties.
     */
    private fun setProperties(props: Properties, values: Map<String, String>): Properties {
        for ((key, value) in values) {
            value.let {
                props.setProperty(StringUtil.toDotCase(key), it)
            }
        }

        return props
    }

    /**
     * Subscribes to a Kafka channel and creates KafkaReceiver instances based on the receiver configuration.
     *
     * @param channel The channel to subscribe to.
     * @return A map of the channel and the associated list of KafkaReceiver instances.
     */
    override fun subscribe(channel: Channel): Map<Channel, List<KafkaReceiver>> {
        val props = setProperties(Properties(), transportConfig.options)

        props["group.id"] = "${StringUtil.toKebabCase(props["group.id"].toString())}-consumer-group"

        val concurrency = channel.receiverConfig.concurrency
        val receivers = List(concurrency) { KafkaReceiver(props, serializer) }

        receivers.forEach { it.subscribe(channel, name) }
        receivers.forEach { receiver -> this.receivers[channel] = this.receivers[channel].orEmpty() + receiver }

        return this.receivers
    }

    /**
     * Retrieves the list of all KafkaReceiver instances associated with the subscribed channels.
     *
     * @return The list of KafkaReceiver instances.
     */
    override fun getReceivers(): List<KafkaReceiver> {
        return receivers.values.flatten()
    }

    /**
     * Performs the acknowledgement of a message envelope.
     *
     * @param envelope The envelope to acknowledge.
     * @return True if the acknowledgement was successful, false otherwise.
     */
    override fun ack(envelope: Envelope<*>): Boolean {
        println("ACKING MESSAGES")
        return true
    }

    /**
     * Rejects a message envelope.
     *
     * @param envelope The envelope to reject.
     */
    override fun reject(envelope: Envelope<Any>) {
        println("REJECTING MESSAGES")
    }

    /**
     * Sends a message envelope to a Kafka channel.
     *
     * @param channel The channel to send the envelope to.
     * @param envelope The envelope to send.
     * @param key The optional key associated with the message.
     * @return The sent envelope.
     */
    override fun send(channel: Channel, envelope: Envelope<*>, key: String?): Envelope<*> {
        getSender().send(channel, envelope, key)
        return envelope
    }
}
