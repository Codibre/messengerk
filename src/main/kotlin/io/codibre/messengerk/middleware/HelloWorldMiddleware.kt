package io.codibre.messengerk.middleware

import io.codibre.messengerk.MiddlewareStack
import io.codibre.messengerk.contracts.Middleware
import io.codibre.messengerk.stamp.HelloStamp

class HelloWorldMiddleware : Middleware {

    override fun handle(
        envelope: io.codibre.messengerk.Envelope<Any>,
        stack: MiddlewareStack
    ): io.codibre.messengerk.Envelope<Any> {
        return stack.next().handle(envelope.with(HelloStamp()), stack)
    }
}
