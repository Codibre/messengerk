package io.codibre.messengerk.contracts

import io.codibre.messengerk.MiddlewareStack

interface Middleware {
    fun handle(envelope: io.codibre.messengerk.Envelope<Any>, stack: MiddlewareStack): io.codibre.messengerk.Envelope<Any>
}
