package io.codibre.messengerk.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class MessageHandler(val priority: Int = 0)
