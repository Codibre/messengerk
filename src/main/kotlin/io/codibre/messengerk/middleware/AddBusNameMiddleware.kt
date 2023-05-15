package io.codibre.messengerk.middleware

import io.codibre.messengerk.Envelope
import io.codibre.messengerk.MiddlewareStack
import io.codibre.messengerk.contracts.Middleware
import io.codibre.messengerk.stamp.BusNameStamp

class AddBusNameMiddleware(private val busName: String) : Middleware {
    override fun handle(envelope: Envelope<Any>, stack: MiddlewareStack): Envelope<Any> {
        val envelopeWithBusName = envelope.with(BusNameStamp(busName))
        return stack.next().handle(envelopeWithBusName, stack)
    }
}
