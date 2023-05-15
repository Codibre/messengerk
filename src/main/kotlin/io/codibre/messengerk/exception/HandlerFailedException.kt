package io.codibre.messengerk.exception

import io.codibre.messengerk.Envelope

class HandlerFailedException(
    val envelope: Envelope<Any>,
    val exceptions: Map<String, Throwable>
) : Throwable("Handling of ${envelope.message::class.qualifiedName} failed: ${exceptions.values}")
