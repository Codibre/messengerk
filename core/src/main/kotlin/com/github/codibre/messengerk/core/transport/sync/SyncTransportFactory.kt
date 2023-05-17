package com.github.codibre.messengerk.core.transport.sync

import com.github.codibre.messengerk.core.MessageBusLocator
import com.github.codibre.messengerk.core.transport.Transport
import com.github.codibre.messengerk.core.transport.TransportConfig
import com.github.codibre.messengerk.core.transport.TransportFactory


/**
 * Factory for creating instances of [SyncTransport].
 *
 * @param busLocator the message bus locator used for creating [SyncTransport] instances
 */
@com.github.codibre.messengerk.core.annotations.TransportFactory
class SyncTransportFactory(private val busLocator: MessageBusLocator) : TransportFactory {
    /**
     * Creates a new [SyncTransport] instance with the specified name and configuration.
     *
     * @param name the name of the transport
     * @param transportConfig the configuration for the transport (not used in this implementation)
     * @return a new instance of [SyncTransport]
     */
    override fun create(
        name: String,
        transportConfig: TransportConfig
    ): Transport {
        return SyncTransport(name = name, busLocator = busLocator)
    }

    /**
     * Checks if the factory supports the specified broker.
     *
     * @param broker the name of the broker
     * @return `true` if the broker is "sync", `false` otherwise
     */
    override fun supports(broker: String): Boolean = broker == "sync"
}
