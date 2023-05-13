package io.codibre.messengerk.middleware

import io.codibre.messengerk.MiddlewareStack
import io.codibre.messengerk.contracts.Middleware
import io.codibre.messengerk.stamp.BusNameStamp

class AddBusNameMiddleware(private val busName: String) : Middleware {
    override fun handle(envelope: io.codibre.messengerk.Envelope<Any>, stack: MiddlewareStack): io.codibre.messengerk.Envelope<Any> {
        val envelopeWithBusName = envelope.with(BusNameStamp(busName))
        return stack.next().handle(envelopeWithBusName, stack)
    }
}
