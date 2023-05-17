package com.github.codibre.messengerk.core.contracts

import com.github.codibre.messengerk.core.Envelope
import com.github.codibre.messengerk.core.MiddlewareStack

interface Middleware {
    fun handle(
        envelope: Envelope<Any>,
        stack: MiddlewareStack
    ): Envelope<Any>
}
