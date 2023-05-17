package com.github.codibre.messengerk.kafka_transport

import com.github.codibre.messengerk.core.Channel
import com.github.codibre.messengerk.core.Envelope
import com.github.codibre.messengerk.core.ReceiverWorker
import com.github.codibre.messengerk.core.serializer.JacksonSerializer
import kotlinx.coroutines.delay
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.ListTopicsOptions
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.KafkaException
import java.time.Duration
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Implementation of the [ReceiverWorker] interface for receiving messages from Kafka.
 *
 * @param properties The Kafka consumer properties.
 * @param serializer The serializer used to deserialize Kafka message values into Envelope objects.
 */
class KafkaReceiver(
    private val properties: Properties,
    private val serializer: JacksonSerializer
) : ReceiverWorker {
    private var subscribed: AtomicBoolean = AtomicBoolean(false)
    private var consumer: KafkaConsumer<String, String>? = null
    private var metadata: MutableMap<String, String> = mutableMapOf()

    /**
     * Receives messages from Kafka.
     *
     * @return A list of Envelope objects containing the received messages.
     * @throws Exception if the Kafka receiver worker is not subscribed to any channel or if the Kafka broker is down or the topic does not exist.
     */
    override suspend fun receive(): List<Envelope<Any>> {
        if (!subscribed.get() || consumer == null) {
            throw Exception("Kafka receiver worker is not subscribed to any channel") //todo: Custom exception
        }

        if (consumer?.partitionsFor(metadata["channel"])?.isEmpty() == true) {
            throw Exception("Kafka broker is down or topic does not exist") //todo: Custom exception
        }

        val records = consumer?.poll(Duration.ofMillis(1))
        val envelopes = mutableListOf<Envelope<Any>>()

        records?.forEach { record ->
            envelopes.add(serializer.decode(record.value()))
        }

        return envelopes
    }

    /**
     * Returns the metadata associated with the Kafka receiver.
     *
     * @return A map containing the metadata.
     */
    override fun metadata(): Map<String, String> {
        return metadata
    }

    /**
     * Subscribes the Kafka receiver to the specified channel.
     *
     * @param channel The channel to subscribe to.
     * @param name The name of the transport.
     * @throws Exception if the Kafka broker is down or could not fetch Kafka metadata.
     */
    fun subscribe(channel: Channel, name: String) {
        if (!isKafkaBrokerUp(properties)) {
            throw Exception("Could not fetch Kafka metadata, broker may be down") // todo: custom exception
        }

        consumer = KafkaConsumer<String, String>(properties)
        consumer!!.subscribe(listOf(channel.name))
        subscribed.set(true)

        metadata["subscribed"] = "true"
        metadata["channel"] = channel.name
        metadata["transport"] = name
    }

    /**
     * Checks if the Kafka broker is up and running by attempting to fetch the Kafka metadata.
     *
     * @param properties The Kafka consumer properties.
     * @return `true` if the Kafka broker is up and running, `false` otherwise.
     */
    private fun isKafkaBrokerUp(properties: Properties): Boolean {
        val adminClient = AdminClient.create(properties)

        return try {
            val options = ListTopicsOptions().timeoutMs(10000)
            adminClient.listTopics(options).listings().get()

            true
        } catch (e: KafkaException) {
            false
        } catch (e: ExecutionException) {
            false
        } finally {
            adminClient.close()
        }
    }

    /**
     * Starts the Kafka receiver and continuously receives messages from Kafka.
     */
    override suspend fun start() {
        while (true) {
            println("Receiving messages from Kafka")
            delay(1000)
            receive()
        }
    }

    /**
     * Stops the Kafka receiver.
     */
    override suspend fun stop() {
        consumer?.close()
    }

    /**
     * Commits the consumed messages in Kafka.
     *
     * @return true if the messages were successfully committed.
     */
    override suspend fun ack(): Boolean {
        consumer?.commitSync()
        return true
    }

    /**
     * Rejects the consumed messages in Kafka.
     *
     * @return false, as message rejection is not supported in Kafka.
     */
    override suspend fun reject(): Boolean {
        return false
    }
}
