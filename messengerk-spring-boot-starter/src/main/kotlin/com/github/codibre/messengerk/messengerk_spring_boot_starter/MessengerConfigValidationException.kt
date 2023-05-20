package com.github.codibre.messengerk.messengerk_spring_boot_starter

import jakarta.validation.ConstraintViolationException

class MessengerConfigValidationException(private val ex: ConstraintViolationException) : Throwable() {
    override val message: String
        get() = "\nMessenger configuration validation error:\n ${ex.message.toString().replace(",","\n")}"
}
