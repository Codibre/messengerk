package com.github.codibre.messengerk.core

import com.github.codibre.messengerk.core.contracts.MessageBus
import com.github.codibre.messengerk.core.contracts.Middleware
import com.github.codibre.messengerk.core.handler.HandlerDescriptor
import com.github.codibre.messengerk.core.handler.HandlerLocator
import com.github.codibre.messengerk.core.middleware.AddBusNameMiddleware
import com.github.codibre.messengerk.core.middleware.HandleMessageMiddleware
import com.github.codibre.messengerk.core.middleware.SendMessageMiddleware
import com.github.codibre.messengerk.core.transport.RouteRegistry
import com.github.codibre.messengerk.core.transport.TransportRegistry


/**
 * Builder class for creating a [MessageBus] instance.
 *
 * @param busName The name of the message bus. Default is "default.bus".
 */
data class MessageBusBuilder(private val busName: String = "default.bus") {

    private var handlerLocator: HandlerLocator = HandlerLocator()
    private val routeRegistry: RouteRegistry = RouteRegistry()
    private val transportRegistry: TransportRegistry = TransportRegistry()
    private var allowNoHandlers: Boolean = false
    private val middlewares: MutableList<Middleware> = mutableListOf()

    /**
     * Adds a handler to the message bus.
     *
     * @param handler The handler descriptor to add.
     * @return This [MessageBusBuilder] instance.
     */
    fun withHandler(handler: HandlerDescriptor): MessageBusBuilder {
        handlerLocator = handlerLocator.withHandler(handler)
        return this
    }

    /**
     * Adds multiple handlers to the message bus.
     *
     * @param handlers The list of handler descriptors to add.
     * @return This [MessageBusBuilder] instance.
     */
    fun withHandlers(handlers: List<HandlerDescriptor>): MessageBusBuilder {
        handlerLocator = handlerLocator.withHandlers(handlers)
        return this
    }

    /**
     * Sets whether the message bus allows no handlers for a message type.
     *
     * @param bool Whether to allow no handlers. Default is false.
     * @return This [MessageBusBuilder] instance.
     */
    fun allowNoHandlers(bool: Boolean): MessageBusBuilder {
        allowNoHandlers = bool
        return this
    }

    /**
     * Sets the route registry for the message bus.
     *
     * @param registry The route registry to set.
     * @return This [MessageBusBuilder] instance.
     */
    fun withRouteRegistry(registry: RouteRegistry): MessageBusBuilder {
        routeRegistry.putAll(registry)
        return this
    }

    /**
     * Sets the transport registry for the message bus.
     *
     * @param registry The transport registry to set.
     * @return This [MessageBusBuilder] instance.
     */
    fun withTransportRegistry(registry: TransportRegistry): MessageBusBuilder {
        transportRegistry.putAll(registry)
        return this
    }

    /**
     * Adds a middleware to the message bus.
     *
     * @param middleware The middleware to add.
     * @return This [MessageBusBuilder] instance.
     */
    fun withMiddleware(middleware: Middleware): MessageBusBuilder {
        middlewares.add(middleware)
        return this
    }

    /**
     * Builds and configures the message bus instance.
     *
     * @param init The optional initialization block for further configuration.
     * @return The configured [MessageBus] instance.
     */
    fun build(init: MessageBusBuilder.() -> Unit): MessageBus {
        withMiddleware(AddBusNameMiddleware(busName = busName))
        init(this)
        if (routeRegistry.isNotEmpty()) {
            withMiddleware(SendMessageMiddleware(routeRegistry = routeRegistry))
        }
        withMiddleware(HandleMessageMiddleware(handlerLocator = handlerLocator, allowNoHandlers = allowNoHandlers))
        return DefaultMessageBus(name = busName, middlewares = middlewares.toList())
    }
}
