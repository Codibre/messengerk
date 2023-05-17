package com.github.codibre.messengerk.kafka_transport

import com.github.codibre.messengerk.core.Channel
import com.github.codibre.messengerk.core.Envelope
import com.github.codibre.messengerk.core.serializer.JacksonSerializer
import com.github.codibre.messengerk.core.transport.Sender
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

/**
 * Kafka implementation of the Sender interface for sending messages to a Kafka topic.
 *
 * @param properties The Kafka producer properties.
 * @param serializer The serializer used to serialize the message envelope.
 */
class KafkaSender(
    properties: Properties,
    private val serializer: JacksonSerializer
) : Sender {
    private val producer: KafkaProducer<String, String>

    init {
        producer = KafkaProducer(properties)
    }

    /**
     * Sends an envelope to the specified Kafka topic.
     *
     * @param channel The channel representing the Kafka topic.
     * @param envelope The message envelope to send.
     * @param key The optional key associated with the message.
     * @return The sent envelope.
     * @throws Exception if an error occurs while sending the message.
     */
    override fun send(channel: Channel, envelope: Envelope<*>, key: String?): Envelope<*> {
        val record = ProducerRecord(channel.name, key, serializer.encode(envelope))

        try {
            producer.send(record) { _, exception ->
                if (exception != null) {
                    throw exception
                }
            }.get() // Wait for the acknowledgement of message delivery
        } catch (ex: Throwable) {
            println("Error sending message to Kafka: ${ex.message}")
            throw ex
        }

        return envelope
    }
}
