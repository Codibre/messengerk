package io.codibre.messengerk.middleware

import io.codibre.messengerk.MiddlewareStack
import io.codibre.messengerk.contracts.Middleware
import io.codibre.messengerk.exception.HandlerFailedException
import io.codibre.messengerk.exception.NoHandlerForMessageException
import io.codibre.messengerk.handler.HandlerDescriptor
import io.codibre.messengerk.handler.HandlerLocator
import io.codibre.messengerk.stamp.HandledStamp

/**
 * Represents a middleware that handles messages in the message bus.
 *
 * This middleware invokes the appropriate handlers based on the message type
 * and manages the handling process. It ensures that each handler is executed
 * in the specified order of priority.
 *
 * @param handlerLocator The handler locator implementation used to retrieve handlers for a message.
 * @param allowNoHandlers Determines whether to allow the absence of handlers for a message.
 */
class HandleMessageMiddleware(
    private val handlerLocator: HandlerLocator, private val allowNoHandlers: Boolean
) : Middleware {

    /**
     * Handles the incoming envelope by invoking the appropriate handlers based on the message type.
     *
     * This method retrieves the handlers from the handler locator for the message type of the envelope.
     * It invokes each handler in the order of their priority, as defined by the `io.codibre.messengerk.annotations.MessageHandler` annotation.
     * If a handler throws an exception, it is captured and stored in the exceptions map.
     * The final envelope is updated with the result of each handler invocation, if any.
     * If no handlers are found for the message type and `allowNoHandlers` is false, a `NoHandlerForMessageException` is thrown.
     * If any exceptions occurred during handler invocations, a `HandlerFailedException` is thrown.
     *
     * @param envelope The incoming envelope to handle.
     * @param stack The middleware stack for further processing.
     * @return The updated envelope after handling.
     * @throws NoHandlerForMessageException If no handlers are found for the message type and `allowNoHandlers` is false.
     * @throws HandlerFailedException If any exceptions occurred during handler invocations.
     */
    override fun handle(envelope: io.codibre.messengerk.Envelope<Any>, stack: MiddlewareStack): io.codibre.messengerk.Envelope<Any> {
        val exceptions = mutableMapOf<String, Throwable>()
        val handlers = handlerLocator.getHandlers(envelope)
        var finalEnvelope: io.codibre.messengerk.Envelope<Any> = envelope.copy()

        // Order handlers by priority
        val sortedHandlers = handlers.sortedBy { handlerDescriptor ->
            handlerDescriptor.kClass.annotations.filterIsInstance<io.codibre.messengerk.annotations.MessageHandler>()
                .firstOrNull()?.priority ?: Int.MAX_VALUE
        }

        for (handler in sortedHandlers) {
            try {
                if (messageHasAlreadyBeenHandled(envelope, handler)) {
                    continue
                }
                val result = handler.callable.invoke(envelope)
                val resultClass = result?.javaClass?.name

                finalEnvelope = finalEnvelope.with(
                    HandledStamp(
                        handler.name,
                        handler.uuid,
                        finalEnvelope.uuid,
                        result,
                        resultClass
                    )
                )

            } catch (e: Throwable) {
                exceptions[handler.name] = e
            }
        }

        if (handlers.isEmpty() && !allowNoHandlers) {
            throw NoHandlerForMessageException("No handler found for message ${envelope.message::class}")
        }

        if (exceptions.isNotEmpty()) {
            throw HandlerFailedException(finalEnvelope, exceptions)
        }

        return stack.next().handle(finalEnvelope, stack)
    }

    /**
     * Checks if the given message in the envelope has already been handled by the specified handler.
     *
     * This method checks if the envelope contains a `HandledStamp` for the given handler.
     * It compares the `handlerId` and `name` properties of the `HandledStamp` with the corresponding properties of the handler descriptor.
     * If a match is found, it indicates that the message has already been handled by the handler.
     *
     * @param envelope The envelope containing the message and stamps.
     * @param handlerDescriptor The handler descriptor to check against.
     * @return `true` if the message has already been handled by the handler, `false` otherwise.
     */
    private fun messageHasAlreadyBeenHandled(
        envelope: io.codibre.messengerk.Envelope<Any>,
        handlerDescriptor: HandlerDescriptor
    ): Boolean {
        for (handledStamp in envelope.allOf<HandledStamp>()) {
            if (handledStamp is HandledStamp
                && handledStamp.handlerId == handlerDescriptor.uuid
                && handlerDescriptor.name == handledStamp.name) {
                return true
            }
        }

        return false
    }
}
