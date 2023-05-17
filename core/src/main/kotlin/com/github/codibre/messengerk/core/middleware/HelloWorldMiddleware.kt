package com.github.codibre.messengerk.core.middleware

import com.github.codibre.messengerk.core.Envelope
import com.github.codibre.messengerk.core.MiddlewareStack
import com.github.codibre.messengerk.core.contracts.Middleware
import com.github.codibre.messengerk.core.stamp.HelloStamp

class HelloWorldMiddleware : Middleware {

    override fun handle(
        envelope: Envelope<Any>,
        stack: MiddlewareStack
    ): Envelope<Any> {
        return stack.next().handle(envelope.with(HelloStamp()), stack)
    }
}
