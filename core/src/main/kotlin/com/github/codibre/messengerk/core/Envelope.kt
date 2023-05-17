package com.github.codibre.messengerk.core

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.codibre.messengerk.core.stamp.Stamp
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
/**
 * Represents an envelope that wraps a message and stamps.
 *
 * @param MessageType The type of the message contained in the envelope.
 * @param message The message to be wrapped in the envelope.
 * @param stamps The collection of stamps associated with the envelope.
 * @param uuid The unique identifier of the envelope.
 */
data class Envelope<MessageType>(
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@modelClass")
    val message: MessageType,
    @Serializable
    val stamps: StampCollection = StampCollection(),
    val uuid: String = UUID.randomUUID().toString()
) {
    init {
        requireNotNull(message) { "Message cannot be null." }
    }

    /**
     * Adds a stamp to the envelope and returns a new envelope with the added stamp.
     *
     * @param stamp The stamp to be added.
     * @return A new envelope with the added stamp.
     */
    fun with(stamp: Stamp): Envelope<MessageType> {
        return Envelope(message, stamps.add(stamp), uuid)
    }

    /**
     * Checks if the envelope contains a stamp of the specified type.
     *
     * @return `true` if the envelope contains a stamp of the specified type, `false` otherwise.
     */
    inline fun <reified StampType> contains(): Boolean {
        return allOf<StampType>().isNotEmpty()
    }

    /**
     * Checks if the envelope does not contain a stamp of the specified type.
     *
     * @return `true` if the envelope does not contain a stamp of the specified type, `false` otherwise.
     */
    inline fun <reified StampType> notContains(): Boolean {
        return allOf<StampType>().isEmpty()
    }

    /**
     * Retrieves all stamps of the specified type from the envelope.
     *
     * @return A list of stamps of the specified type.
     */
    inline fun <reified StampType> allOf(): List<Stamp> {
        return this.stamps.allOf(StampType::class)
    }

    /**
     * Retrieves the last stamp of the specified type from the envelope.
     *
     * @return The last stamp of the specified type, or `null` if not found.
     */
    inline fun <reified StampType> lastOf(): Stamp? {
        return this.stamps.lastOf(StampType::class)
    }

    /**
     * Retrieves the first stamp of the specified type from the envelope.
     *
     * @return The first stamp of the specified type, or `null` if not found.
     */
    inline fun <reified StampType> firstOf(): Stamp? {
        return this.stamps.firstOf(StampType::class)
    }

    /**
     * Checks if the envelope has no stamps.
     *
     * @return `true` if the envelope has no stamps, `false` otherwise.
     */
    fun hasNoStamps(): Boolean {
        return this.stamps.isEmpty
    }

    /**
     * Checks if the envelope has stamps.
     *
     * @return `true` if the envelope has stamps, `false` otherwise.
     */
    fun hasStamps(): Boolean {
        return this.stamps.isNotEmpty
    }
}
