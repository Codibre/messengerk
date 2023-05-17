package com.github.codibre.messengerk.core.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class MessageHandler(val priority: Int = 0)
