package com.github.Codibre.messengerk.transport

/**
 * Represents a registry for message routes.
 *
 * The registry maps message types to router locators, which provide the routes for message delivery.
 * The router locator is a function that returns a [Route] for a given message type.
 */
class RouteRegistry : HashMap<String, RouterLocator>()

/**
 * Represents a function that provides a [Route] for a given message type.
 */
typealias RouterLocator = () -> Route
