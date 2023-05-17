package com.github.codibre.messengerk.core.contracts

import com.github.codibre.messengerk.core.Envelope
import com.github.codibre.messengerk.core.StampCollection
import com.github.codibre.messengerk.core.stamp.Stamp

/**
 * Represents a message bus that handles the dispatch of messages.
 */
interface MessageBus {
    /**
     * The name of the message bus.
     */
    val name: String

    /**
     * Dispatches a message with optional stamps.
     *
     * @param message The message to be dispatched.
     * @param stamps Optional list of stamps to be added to the envelope.
     * @return Result object containing the dispatched envelope.
     */
    fun dispatch(message: Any, vararg stamps: Stamp): Result<Envelope<Any>> {
        var envelope = if (message is Envelope<*>) {
            @Suppress("UNCHECKED_CAST")
            message as Envelope<Any>
        } else {
            Envelope(message, StampCollection.buildFromList(stamps.toList()))
        }

        stamps.forEach { stamp ->
            envelope = envelope.with(stamp)
        }

        return dispatch(envelope)
    }

    /**
     * Dispatches an envelope.
     *
     * @param envelope The envelope to be dispatched.
     * @return Result object containing the dispatched envelope.
     */
    fun dispatch(envelope: Envelope<Any>): Result<Envelope<Any>>
}
