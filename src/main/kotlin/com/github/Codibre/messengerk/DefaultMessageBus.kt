package com.github.Codibre.messengerk

import com.github.Codibre.messengerk.contracts.MessageBus
import com.github.Codibre.messengerk.contracts.Middleware
import com.github.Codibre.messengerk.exception.HandlerFailedException


class DefaultMessageBus(
    override val name: String = "default",
    private val middlewares: List<Middleware> = listOf()
) : MessageBus {

    override fun dispatch(envelope: Envelope<Any>): Result<Envelope<Any>> {

        val stack = MiddlewareStack(middlewares)

        return try {
            return Result.success(stack.current.handle(envelope, stack))
        } catch (e: HandlerFailedException) {
            // Se for async (ex: vinda de um worker) não tem sentido dar retorno
            // Se for sync, pode ser que se espere retorno, dai usaremos o Result pattern
            // todo: fazer tratamento de DLQ
            println("Handler failed error: ${e.message}")
            Result.failure(e)
        } catch (e: Throwable) {
            println("Unexpected error: ${e.message}")
            Result.failure(e)
        }
    }
}
