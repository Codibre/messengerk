package com.github.codibre.messengerk.core.middleware

import com.github.codibre.messengerk.core.Envelope
import com.github.codibre.messengerk.core.MiddlewareStack
import com.github.codibre.messengerk.core.contracts.Middleware
import com.github.codibre.messengerk.core.stamp.BusNameStamp

class AddBusNameMiddleware(private val busName: String) : Middleware {
    override fun handle(
        envelope: Envelope<Any>,
        stack: MiddlewareStack
    ): Envelope<Any> {
        val envelopeWithBusName = envelope.with(BusNameStamp(busName))
        return stack.next().handle(envelopeWithBusName, stack)
    }
}
