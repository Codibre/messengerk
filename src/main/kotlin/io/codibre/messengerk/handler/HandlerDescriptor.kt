package io.codibre.messengerk.handler

import java.lang.reflect.Method
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.javaMethod

/**
 * Represents a descriptor for a message handler method.
 *
 * This class encapsulates information about a message handler method, including its name, KFunction representation,
 * and the message type it handles. Each handler descriptor is associated with a unique identifier (UUID).
 *
 * @property callable The callable representing the message handler.
 * @property uuid The unique identifier (UUID) for the handler descriptor.
 * @property name The name of the handler method.
 * @property kClass The KFunction representation of the handler method.
 * @property messageType The type of message that the handler method handles.
 */
class HandlerDescriptor(
    handler: Method,
    val callable: MessageHandler,
    val uuid: String = UUID.randomUUID().toString()
) {
    val name: String
    val kClass: KFunction<*>
    val messageType: String

    init {
        name = handler.name
        kClass = handler.toKFunction()
        messageType = extractMessageType(kClass)
    }

    /**
     * Extracts the message type from the handler method.
     *
     * This method inspects the parameter type of the handler method and extracts the message type.
     * It assumes that the message type is the first type argument of the parameter type.
     *
     * @param handler The KFunction representing the handler method.
     * @return The extracted message type.
     * @throws Exception if the message type cannot be extracted from the listener.
     */
    private fun extractMessageType(handler: KFunction<*>): String {
        val parameterType = handler.parameters.getOrNull(1)?.type
        val typeArgument = parameterType?.arguments?.getOrNull(0)?.type
        return typeArgument?.toString()
            ?: throw Exception("Could not extract messageType from listener: ${handler.parameters.getOrNull(1)?.type}")
    }

    /**
     * Method extension to convert from a Method to a KFunction
     *
     * @return The corresponding KFunction.
     * @throws IllegalArgumentException if no corresponding Kotlin function is found for the given Java method.
     */
    private fun Method.toKFunction(): KFunction<*> {
        val declaringClass = declaringClass.kotlin
        return declaringClass.declaredFunctions.find { it.javaMethod == this }
            ?: throw IllegalArgumentException("No corresponding Kotlin function found for the given Java method.")
    }

    companion object {
        /**
         * Creates a HandlerDescriptor from a KFunction.
         *
         * This factory method creates a HandlerDescriptor from the given KFunction.
         * It generates a unique identifier (UUID) for the descriptor and creates a callable that invokes the KFunction.
         *
         * @param function The KFunction representing the handler method.
         * @return The created HandlerDescriptor.
         * @throws IllegalArgumentException if no corresponding Java method is found for the given Kotlin function.
         */
        fun fromKFunction(function: KFunction<*>): HandlerDescriptor {
            val javaMethod = function.javaMethod
                ?: throw IllegalArgumentException("No corresponding Java method found for the given Kotlin function.")

            val callable: MessageHandler = { envelope ->
                val instance = javaMethod.declaringClass.getDeclaredConstructor().newInstance()
                function.call(instance, envelope)
            }

            return HandlerDescriptor(javaMethod, callable)
        }
    }
}
