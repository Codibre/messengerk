package com.github.codibre.messengerk.kafka_transport

import com.github.codibre.messengerk.core.transport.Transport
import com.github.codibre.messengerk.core.transport.TransportConfig
import com.github.codibre.messengerk.core.transport.TransportFactory


/**
 * A factory for creating KafkaTransport instances.
 */
@com.github.codibre.messengerk.core.annotations.TransportFactory
class KafkaTransportFactory : TransportFactory {
    /**
     * Creates a new KafkaTransport instance.
     *
     * @param name The name of the transport.
     * @param transportConfig The configuration for the transport.
     * @return The created KafkaTransport instance.
     */
    override fun create(name: String, transportConfig: TransportConfig): Transport {
        return KafkaTransport(name, transportConfig)
    }

    /**
     * Checks if the factory supports the given broker.
     *
     * @param broker The name of the broker.
     * @return `true` if the factory supports the broker, `false` otherwise.
     */
    override fun supports(broker: String): Boolean {
        return broker == "kafka"
    }
}
