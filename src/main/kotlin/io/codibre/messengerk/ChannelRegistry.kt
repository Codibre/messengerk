package io.codibre.messengerk

/**
 * Type alias for a channel locator function.
 */
typealias ChannelLocator = () -> Channel

/**
 * Registry for channels.
 */
class ChannelRegistry : HashMap<String, ChannelLocator>()
