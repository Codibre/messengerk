package io.codibre.messengerk.middleware

import io.codibre.messengerk.Envelope
import io.codibre.messengerk.MiddlewareStack
import io.codibre.messengerk.annotations.MessageHandler
import io.codibre.messengerk.exception.HandlerFailedException
import io.codibre.messengerk.exception.NoHandlerForMessageException
import io.codibre.messengerk.handler.HandlerDescriptor
import io.codibre.messengerk.handler.HandlerLocator
import io.codibre.messengerk.stamp.HandledStamp
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class HandlerMiddlewareTest {

    class FooMessage

    class FooHandler {
        @MessageHandler
        fun handleFoo(envelope: Envelope<in FooMessage>): Envelope<in FooMessage> {
            print("Handling FooMessage")
            return envelope
        }
    }

    class FooHandlerFail {
        @MessageHandler
        fun handleFooFail(envelope: Envelope<in FooMessage>): Envelope<in FooMessage> {
            println("Processing ${envelope.message}")
            throw Exception("handler failed")
        }
    }

    @Test
    fun `it should have a handledStamp when handling a message`() {
        @Suppress("UNCHECKED_CAST")
        val envelope =
            Envelope(FooMessage()) as Envelope<Any>
        val className = envelope.message::class.qualifiedName!!
        val handlers = mutableMapOf(className to mutableListOf(HandlerDescriptor.fromKFunction(FooHandler::handleFoo)))
        val handlerLocator = HandlerLocator(handlers)
        val middleware = HandleMessageMiddleware(handlerLocator, false)
        val handledEnvelope = middleware.handle(envelope, MiddlewareStack(listOf(middleware)))
        val stamp = handledEnvelope.firstOf<HandledStamp>() as HandledStamp

        assertTrue(stamp.name == FooHandler::handleFoo.name)
    }

    @Test
    fun `it should throw a NoHandlerForMessageException when there is no handler available and allowNoHandlers is false`() {
        @Suppress("UNCHECKED_CAST")
        val envelope =
            Envelope(FooMessage()) as Envelope<Any>
        val handlers: MutableMap<String, MutableList<HandlerDescriptor>> = mutableMapOf()
        val handlerLocator = HandlerLocator(handlers)
        val middleware = HandleMessageMiddleware(handlerLocator, false)

        assertThrows<NoHandlerForMessageException> {
            middleware.handle(
                envelope,
                MiddlewareStack(listOf(middleware))
            )
        }
    }

    @Test
    fun `it should ignore when a message has already been handled`() {
        @Suppress("UNCHECKED_CAST")
        val envelope =
            Envelope(FooMessage()) as Envelope<Any>
        val className = envelope.message::class.qualifiedName!!
        val handlers = mutableMapOf(className to mutableListOf(HandlerDescriptor.fromKFunction(FooHandler::handleFoo)))
        val handlerLocator = HandlerLocator(handlers)
        val middleware = HandleMessageMiddleware(handlerLocator, false)
        val handledEnvelope = middleware.handle(envelope, MiddlewareStack(listOf(middleware)))
        val stamp = handledEnvelope.firstOf<HandledStamp>() as HandledStamp
        assertTrue(stamp.name == FooHandler::handleFoo.name)

        // calling the stack again with the handled message
        val secondHandledEnvelope = middleware.handle(handledEnvelope, MiddlewareStack(listOf(middleware)))
        assertTrue(handledEnvelope == secondHandledEnvelope)
    }

    @Test
    fun `it throws exception on a failed handler execution`() {
        @Suppress("UNCHECKED_CAST")
        val envelope =
            Envelope(FooMessage()) as Envelope<Any>
        val className = envelope.message::class.qualifiedName!!
        val handlers = mutableMapOf(className to mutableListOf(HandlerDescriptor.fromKFunction(FooHandlerFail::handleFooFail)))
        val handlerLocator = HandlerLocator(handlers)
        val middleware = HandleMessageMiddleware(handlerLocator, false)

        val exception = assertThrows<HandlerFailedException> {
            middleware.handle(
                envelope,
                MiddlewareStack(listOf(middleware))
            )
        }

        assertTrue(exception.exceptions.count() == 1)
        assertTrue(exception.envelope == envelope)
        assertTrue(exception.envelope.notContains<HandledStamp>())
    }

    @Test
    fun `it throws exception on a partial failed handler execution`() {
        @Suppress("UNCHECKED_CAST")
        val envelope =
            Envelope(FooMessage()) as Envelope<Any>
        val handlerLocator: HandlerLocator =
            HandlerLocator().withHandlers(
                listOf(
                    HandlerDescriptor.fromKFunction(FooHandler::handleFoo),
                    HandlerDescriptor.fromKFunction(FooHandlerFail::handleFooFail)
                )
            )

        val middleware = HandleMessageMiddleware(handlerLocator, false)
        val exception = assertThrows<HandlerFailedException> {
            middleware.handle(
                envelope,
                MiddlewareStack(listOf(middleware))
            )
        }

        assertTrue(exception.exceptions.count() == 1)
        assertTrue(exception.envelope.contains<HandledStamp>())
    }
}
