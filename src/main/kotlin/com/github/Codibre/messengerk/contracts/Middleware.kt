package com.github.Codibre.messengerk.contracts

import com.github.Codibre.messengerk.Envelope
import com.github.Codibre.messengerk.MiddlewareStack

interface Middleware {
    fun handle(envelope: Envelope<Any>, stack: MiddlewareStack): Envelope<Any>
}
