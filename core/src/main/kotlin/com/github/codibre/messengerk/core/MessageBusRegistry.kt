package com.github.codibre.messengerk.core

import com.github.codibre.messengerk.core.contracts.MessageBus

/**
 * Type alias for a function that locates a [MessageBus].
 */
typealias MessageBusLocator = () -> MessageBus

/**
 * Registry for storing [MessageBusLocator] instances with their associated names.
 */
class MessageBusRegistry : HashMap<String, MessageBusLocator>()
