package com.github.Codibre.messengerk.middleware

import com.github.Codibre.messengerk.Envelope
import com.github.Codibre.messengerk.MiddlewareStack
import com.github.Codibre.messengerk.contracts.Middleware
import com.github.Codibre.messengerk.stamp.HelloStamp

class HelloWorldMiddleware : Middleware {

    override fun handle(
        envelope: Envelope<Any>,
        stack: MiddlewareStack
    ): Envelope<Any> {
        return stack.next().handle(envelope.with(HelloStamp()), stack)
    }
}
