package com.github.codibre.messengerk.core.handler

import com.github.codibre.messengerk.core.Envelope

/**
 * Implementation of the [HandlerLocator] interface that stores and retrieves handler descriptors based on message types.
 *
 * This class provides the ability to store and retrieve handler descriptors for different message types.
 * It maintains a mutable map where the keys are message types and the values are lists of handler descriptors.
 *
 * @property handlers The mutable map storing the handler descriptors.
 */
class HandlerLocator(
    private val handlers: MutableMap<String, MutableList<HandlerDescriptor>> = mutableMapOf()
) {

    /**
     * Retrieves the list of handler descriptors for the given envelope's message type.
     *
     * This method retrieves the list of handler descriptors associated with the message type of the envelope's message.
     * If no handlers are found for the message type, an empty list is returned.
     *
     * @param envelope The envelope containing the message.
     * @return The list of handler descriptors for the message type.
     * @throws Exception if the envelope has no message.
     */
    fun getHandlers(envelope: Envelope<*>): List<HandlerDescriptor> {
        if (envelope.message == null) {
            throw Exception("Envelope has no message")
        }

        val type = envelope.message::class.qualifiedName
        return handlers[type] ?: listOf()
    }

    /**
     * Creates a new [HandlerLocator] with the given handler descriptor added.
     *
     * This method creates a new instance of [HandlerLocator] by copying the existing handlers map,
     * and adds the specified handler descriptor to the list of handlers associated with its message type.
     *
     * @param handler The handler descriptor to be added.
     * @return A new [HandlerLocator] instance with the handler added.
     */
    fun withHandler(handler: HandlerDescriptor): HandlerLocator {
        val locator = HandlerLocator(handlers.toMutableMap())

        val messageType = handler.messageType
        val handlerList = locator.handlers.getOrPut(messageType) { mutableListOf() }
        handlerList.add(handler)

        return locator
    }

    /**
     * Creates a new [HandlerLocator] with the given list of handler descriptors added.
     *
     * This method creates a new instance of [HandlerLocator] by copying the existing handlers map,
     * and adds each handler descriptor from the specified list to the corresponding list of handlers
     * associated with their message types.
     *
     * @param handlers The list of handler descriptors to be added.
     * @return A new [HandlerLocator] instance with the handlers added.
     */
    fun withHandlers(handlers: List<HandlerDescriptor>): HandlerLocator {
        var locator = HandlerLocator(this.handlers.toMutableMap())

        for (handler in handlers) {
            locator = locator.withHandler(handler)
        }

        return locator
    }
}
