package com.github.Codibre.messengerk.exception

import com.github.Codibre.messengerk.Envelope

class HandlerFailedException(
    val envelope: Envelope<Any>,
    val exceptions: Map<String, Throwable>
) : Throwable("Handling of ${envelope.message::class.qualifiedName} failed: ${exceptions.values}")
