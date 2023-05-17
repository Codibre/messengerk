package com.github.codibre.messengerk.core.stamp

data class HandledStamp(
    val name: String,
    val handlerId: String,
    val envelopeId: String,
    val result: Any?,
    val resultClass: String?
) : Stamp()
