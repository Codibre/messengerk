package io.codibre.messengerk.transport.memory

import io.codibre.messengerk.Channel
import io.codibre.messengerk.Envelope
import io.codibre.messengerk.ReceiverWorker
import io.codibre.messengerk.serializer.JacksonSerializer
import io.codibre.messengerk.stamp.TransportMessageIdStamp
import io.codibre.messengerk.transport.Transport


/**
 * An implementation of [Transport] that simulates a transport layer in memory.
 *
 * @param name the name of the transport layer
 * @param broker the name of the broker (default: "memory")
 * @param serializer the serializer to use (default: JacksonSerializer)
 */
class InMemoryTransport(
    override val name: String,
    val broker: String = "memory",
    private val serializer: JacksonSerializer = JacksonSerializer(),
) : Transport {

    private val queue: MutableMap<Int, String> = mutableMapOf()
    private val sent: MutableList<String> = mutableListOf()
    private val acknowledged: MutableList<String> = mutableListOf()
    private val rejected: MutableList<String> = mutableListOf()
    private var messageId = 0

    /**
     * Decodes the messages in the queue and returns them as a list of [Envelope]s.
     */
    private fun decode(queue: Map<Int, String>): List<Envelope<Any>> {
        return queue.map { serializer.decode(it.value) }
    }

    /**
     * Decodes a list of messages and returns them as a list of [Envelope]s.
     */
    private fun decode(queue: List<String>): List<Envelope<Any>> {
        return queue.map { serializer.decode(it) }
    }

    /**
     * Encodes an [Envelope] and returns it as a string.
     */
    private fun <T> encode(envelope: Envelope<T>): String {
        return serializer.encode(envelope)
    }

    /**
     * Sends an [Envelope] to a channel.
     *
     * @param channel the channel to send the message to
     * @param envelope the [Envelope] to send
     * @param key an optional routing key
     * @return the sent [Envelope] with the [TransportMessageIdStamp] added to it
     */
    override fun send(channel: Channel, envelope: Envelope<*>, key: String?): Envelope<*> {
        val id = ++messageId
        val newEnvelope = envelope.with(TransportMessageIdStamp(id))
        val encodedEnvelope = encode(newEnvelope)

        sent.add(encodedEnvelope)
        queue[id] = encodedEnvelope

        return newEnvelope
    }

    /**
     * Receives messages from a channel.
     *
     * @param channel the channel to receive messages from
     * @return a list of [Envelope]s
     */
    override suspend fun receive(channel: Channel): List<Envelope<Any>> {
        return decode(queue)
    }

    /**
     * Subscribes to a channel.
     *
     * @param channel the channel to subscribe to
     * @return an empty map
     */
    override fun subscribe(channel: Channel): Map<Channel, List<ReceiverWorker>> {
        return mapOf()
    }

    /**
     * Gets a list of receiver workers.
     *
     * @return an empty list
     */
    override fun getReceivers(): List<ReceiverWorker> {
        return listOf()
    }

    /**
     * Acknowledges receipt of an [Envelope].
     *
     * @param envelope the [Envelope] to acknowledge
     * @return true if the acknowledgement was successful, false otherwise
     * @throws Exception if no [TransportMessageIdStamp] is found on the [Envelope]
     */
    override fun ack(envelope: Envelope<*>): Boolean {
        acknowledged.add(serializer.encode(envelope))
        val transportStamp = (envelope.lastOf<TransportMessageIdStamp>()
            ?: throw Exception("No TransportMessageIdStamp found on the Envelope.")) as TransportMessageIdStamp
        return queue.remove(transportStamp.id) != null
    }

    /**
     * Rejects an [Envelope].
     *
     * @param envelope the [Envelope] to reject
     * @throws Exception if no [TransportMessageIdStamp] is found on the [Envelope]
     */
    override fun reject(envelope: Envelope<Any>) {
        rejected.add(serializer.encode(envelope))
        val transportStamp = (envelope.lastOf<TransportMessageIdStamp>()
            ?: throw Exception("No TransportMessageIdStamp found on the Envelope.")) as TransportMessageIdStamp

        queue.remove(transportStamp.id)
    }

    /**
     * Returns a list of [Envelope]s that have been acknowledged.
     *
     * @return a list of acknowledged [Envelope]s
     */
    fun acknowledged(): List<Envelope<Any>> {
        return decode(acknowledged)
    }

    /**
     * Returns a list of [Envelope]s that have been rejected.
     *
     * @return a list of rejected [Envelope]s
     */
    fun rejected(): List<Envelope<Any>> {
        return decode(rejected)
    }

    /**
     * Returns a list of [Envelope]s that have been sent.
     *
     * @return a list of sent [Envelope]s
     */
    fun sent(): List<Envelope<Any>> {
        return decode(sent)
    }
}
