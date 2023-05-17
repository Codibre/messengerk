package com.github.codibre.messengerk.core.handler

import com.github.codibre.messengerk.core.Envelope

/**
 * Typealias for a callable representing a message handler function.
 *
 * A `MessageHandlerCallable` is a function that takes an [Envelope] containing a message of type [Any] and
 * returns an [Any] value. It represents a callable object that can handle and process messages.
 *
 * @param (envelope) The envelope containing the message to be handled.
 * @return The result of handling the message, which can be of any type.
 */
typealias MessageHandler = (envelope: Envelope<Any>) -> Any?
