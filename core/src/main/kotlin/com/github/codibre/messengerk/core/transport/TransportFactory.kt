package com.github.codibre.messengerk.core.transport

/**
 * Registry for storing transport locators.
 */
interface TransportFactory {
    /**
     * Creates a new transport instance based on the provided name and transport configuration.
     *
     * @param name The name of the transport.
     * @param transportConfig The configuration for the transport.
     * @return The created transport instance.
     */
    fun create(name: String, transportConfig: TransportConfig): Transport

    /**
     * Checks if the factory supports the specified broker.
     *
     * @param broker The name of the broker.
     * @return `true` if the factory supports the broker, `false` otherwise.
     */
    fun supports(broker: String): Boolean
}
