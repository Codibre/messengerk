package com.github.codibre.messengerk.core.transport

/**
 * A typealias representing a function that returns a transport instance.
 */
typealias TransportLocator = () -> Transport

/**
 * Registry for storing transport locators.
 */
class TransportRegistry : HashMap<String, TransportLocator>()
