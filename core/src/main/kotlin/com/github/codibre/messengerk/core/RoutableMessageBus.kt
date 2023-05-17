package com.github.codibre.messengerk.core

import com.github.codibre.messengerk.core.contracts.MessageBus
import com.github.codibre.messengerk.core.exception.BusNotFoundException
import com.github.codibre.messengerk.core.stamp.BusNameStamp


/**
 * A message bus implementation that routes messages to different message buses based on the bus name stamp.
 *
 * @property busRegistry The registry of available message buses.
 */
class RoutableMessageBus(private val busRegistry: MessageBusRegistry) : MessageBus {

    /**
     * The name of the bus router.
     */
    override val name: String
        get() = "busRouter"

    /**
     * Dispatches the given envelope to the appropriate message bus based on the bus name stamp.
     *
     * @param envelope The envelope to be dispatched.
     * @return Result containing the dispatched envelope.
     * @throws BusNotFoundException if the specified bus name is not found in the registry.
     */
    override fun dispatch(envelope: Envelope<Any>): Result<Envelope<Any>> {
        var busNameStamp = envelope.lastOf<BusNameStamp>() as BusNameStamp?

        if (busNameStamp === null) {
            busNameStamp = BusNameStamp("default.bus")
        }

        val busLocator = busRegistry[busNameStamp.name]

        if (busLocator === null) {
            throw BusNotFoundException("Bus with name: ${busNameStamp.name} does not exist")
        }

        return busLocator().dispatch(envelope)
    }
}
