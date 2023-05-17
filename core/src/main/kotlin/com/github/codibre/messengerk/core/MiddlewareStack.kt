package com.github.codibre.messengerk.core

import com.github.codibre.messengerk.core.contracts.Middleware

/**
 * Represents a stack of middlewares to be executed in order.
 *
 * @property middlewares The list of middlewares in the stack.
 */
class MiddlewareStack(private val middlewares: List<Middleware> = emptyList()) {
    private var currentIndex: Int = 0

    /**
     * Retrieves the current middleware in the stack.
     *
     * If the index is out of bounds, a default middleware is returned that simply passes through the envelope unchanged.
     *
     * @return The current middleware.
     */
    val current: Middleware
        get() = middlewares.getOrNull(currentIndex) ?: DefaultMiddleware

    /**
     * Moves to the next middleware in the stack and returns it.
     *
     * @return The next middleware in the stack.
     */
    fun next(): Middleware {
        currentIndex++
        return current
    }

    private object DefaultMiddleware : Middleware {
        override fun handle(
            envelope: Envelope<Any>,
            stack: MiddlewareStack
        ): Envelope<Any> {
            return envelope
        }
    }
}
