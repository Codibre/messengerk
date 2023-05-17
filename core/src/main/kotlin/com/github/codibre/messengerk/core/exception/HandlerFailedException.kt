package com.github.codibre.messengerk.core.exception

import com.github.codibre.messengerk.core.Envelope

class HandlerFailedException(
    val envelope: Envelope<Any>,
    val exceptions: Map<String, Throwable>
) : Throwable("Handling of ${envelope.message::class.qualifiedName} failed: ${exceptions.values}")
