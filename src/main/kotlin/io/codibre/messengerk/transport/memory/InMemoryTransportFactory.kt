package io.codibre.messengerk.transport.memory

import io.codibre.messengerk.transport.Transport
import io.codibre.messengerk.transport.TransportConfig
import io.codibre.messengerk.transport.TransportFactory


/**
 * Factory for creating an instance of [InMemoryTransport].
 */
@io.codibre.messengerk.annotations.TransportFactory
class InMemoryTransportFactory : TransportFactory {
    /**
     * Creates an instance of [InMemoryTransport].
     *
     * @param name the name of the transport
     * @param transportConfig the configuration for the transport (not used in this implementation)
     * @return the created [InMemoryTransport] instance
     */
    override fun create(
        name: String,
        transportConfig: TransportConfig
    ): Transport {
        return InMemoryTransport(name = name)
    }

    /**
     * Checks if the factory supports the given broker type.
     *
     * @param broker the broker type to check
     * @return `true` if the factory supports the broker type, `false` otherwise
     */
    override fun supports(broker: String): Boolean = broker == "memory"
}
