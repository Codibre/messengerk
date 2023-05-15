package io.codibre.messengerk.contracts

import io.codibre.messengerk.Envelope
import io.codibre.messengerk.MiddlewareStack

interface Middleware {
    fun handle(envelope: Envelope<Any>, stack: MiddlewareStack): Envelope<Any>
}
