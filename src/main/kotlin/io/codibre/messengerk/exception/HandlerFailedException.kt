package io.codibre.messengerk.exception

class HandlerFailedException(
    val envelope: io.codibre.messengerk.Envelope<Any>,
    val exceptions: Map<String, Throwable>
) : Throwable("Handling of ${envelope.message::class.qualifiedName} failed: ${exceptions.values}")
