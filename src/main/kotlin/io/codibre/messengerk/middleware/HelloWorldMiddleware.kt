package io.codibre.messengerk.middleware

import io.codibre.messengerk.Envelope
import io.codibre.messengerk.MiddlewareStack
import io.codibre.messengerk.contracts.Middleware
import io.codibre.messengerk.stamp.HelloStamp

class HelloWorldMiddleware : Middleware {

    override fun handle(
        envelope: Envelope<Any>,
        stack: MiddlewareStack
    ): Envelope<Any> {
        return stack.next().handle(envelope.with(HelloStamp()), stack)
    }
}
