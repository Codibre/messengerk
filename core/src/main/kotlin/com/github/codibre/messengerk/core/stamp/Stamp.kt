package com.github.codibre.messengerk.core.stamp

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kotlinx.serialization.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = BusNameStamp::class, name = "BusNameStamp"),
    JsonSubTypes.Type(value = HandledStamp::class, name = "HandledStamp"),
    JsonSubTypes.Type(value = HelloStamp::class, name = "HelloStamp"),
    JsonSubTypes.Type(value = TransportMessageIdStamp::class, name = "TransportMessageIdStamp"),
    JsonSubTypes.Type(value = ReceivedStamp::class, name = "ReceivedStamp"),
    JsonSubTypes.Type(value = SentStamp::class, name = "SentStamp"),
    JsonSubTypes.Type(value = FooStamp::class, name = "FooStamp"),
    JsonSubTypes.Type(value = JohnStamp::class, name = "JohnStamp"),
    JsonSubTypes.Type(value = KeyStamp::class, name = "KeyStamp"),
)
@Serializable
sealed class Stamp
