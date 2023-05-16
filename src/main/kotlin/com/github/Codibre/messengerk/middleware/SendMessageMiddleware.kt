package com.github.Codibre.messengerk.middleware

import com.github.Codibre.messengerk.Envelope
import com.github.Codibre.messengerk.MiddlewareStack
import com.github.Codibre.messengerk.contracts.Middleware
import com.github.Codibre.messengerk.stamp.KeyStamp
import com.github.Codibre.messengerk.stamp.ReceivedStamp
import com.github.Codibre.messengerk.stamp.SentStamp
import com.github.Codibre.messengerk.transport.RouteRegistry
import java.util.*

/**
 * Middleware for sending a message to the appropriate route based on the message type.
 *
 * The `SendMessageMiddleware` is responsible for handling the outgoing messages by determining the appropriate
 * route based on the message type and sending the message using the corresponding transport. It checks if the
 * message has already been marked as received and skips processing if it has.
 *
 * @param routeRegistry The registry containing the routes for different message types.
 */
class SendMessageMiddleware(
    private val routeRegistry: RouteRegistry
) : Middleware {

    /**
     * Handles the outgoing message by sending it to the appropriate route.
     *
     * This method is invoked by the middleware stack to handle the outgoing message. It first checks if the message
     * has already been marked as received, and if so, it skips processing and passes the envelope to the next middleware
     * in the stack. If the message has not been marked as received, it determines the appropriate route based on the
     * message type and sends the message using the corresponding transport.
     *
     * @param envelope The envelope containing the outgoing message.
     * @param stack The middleware stack for further processing.
     * @return The modified envelope after handling the message.
     * @throws IllegalArgumentException if the message type does not have a corresponding route and no default route is available.
     */
    override fun handle(
        envelope: Envelope<Any>,
        stack: MiddlewareStack
    ): Envelope<Any> {
        val messageType =
            envelope.message::class.qualifiedName ?: throw IllegalArgumentException("Envelope has no message type")

        if (envelope.contains<ReceivedStamp>()) {
            return stack.next().handle(envelope, stack)
        }

        val route = routeRegistry[messageType]?.invoke() ?: routeRegistry["default"]?.invoke()
        ?: throw IllegalArgumentException("No route found for message type: $messageType")

        val keyStamp = envelope.firstOf<KeyStamp>() as KeyStamp?
        val sentEnvelope = envelope.with(SentStamp(route.channel.name, route.transport.name))

        @Suppress("UNCHECKED_CAST")
        return route.transport.send(
            route.channel,
            sentEnvelope,
            keyStamp?.key ?: UUID.randomUUID().toString()
        ) as Envelope<Any>
    }
}
