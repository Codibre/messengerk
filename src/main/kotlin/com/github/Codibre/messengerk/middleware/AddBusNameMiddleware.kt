package com.github.Codibre.messengerk.middleware

import com.github.Codibre.messengerk.Envelope
import com.github.Codibre.messengerk.MiddlewareStack
import com.github.Codibre.messengerk.contracts.Middleware
import com.github.Codibre.messengerk.stamp.BusNameStamp

class AddBusNameMiddleware(private val busName: String) : Middleware {
    override fun handle(envelope: Envelope<Any>, stack: MiddlewareStack): Envelope<Any> {
        val envelopeWithBusName = envelope.with(BusNameStamp(busName))
        return stack.next().handle(envelopeWithBusName, stack)
    }
}
