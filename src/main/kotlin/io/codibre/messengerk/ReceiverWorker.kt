package io.codibre.messengerk

interface ReceiverWorker {
    /**
     * Starts the receiver worker, allowing it to begin processing messages.
     */
    suspend fun start()

    /**
     * Stops the receiver worker, halting message processing.
     */
    suspend fun stop()

    /**
     * Receives a batch of messages from the channel.
     *
     * @return a list of received envelopes.
     */
    suspend fun receive(): List<Envelope<Any>>

    /**
     * Acknowledges the last received message, indicating successful processing.
     *
     * @return true if the acknowledgment is successful, false otherwise.
     */
    suspend fun ack(): Boolean

    /**
     * Rejects the last received message, indicating failure or inability to process.
     *
     * @return true if the rejection is successful, false otherwise.
     */
    suspend fun reject(): Boolean

    /**
     * Retrieves the metadata associated with the receiver worker.
     *
     * @return a map containing the metadata key-value pairs.
     */
    fun metadata(): Map<String, String>
}
