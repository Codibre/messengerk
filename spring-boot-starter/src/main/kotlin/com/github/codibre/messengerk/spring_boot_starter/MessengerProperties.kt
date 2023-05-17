package com.github.codibre.messengerk.spring_boot_starter


import com.github.codibre.messengerk.core.contracts.Middleware
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "messenger")
data class MessengerProperties(
    @field:Valid
    var buses: List<BusProperties>? = null,
    @field:Valid
    var channels: List<ChannelProperties>? = null,
    @field:Valid
    var transports: List<TransportProperties>? = null
) {
    @Validated
    data class BusProperties(
        @field:NotEmpty
        var name: String? = null,
        @field:Valid
        var middlewares: List<Middleware>? = null,
        var allowNoHandler: Boolean = true
    )

    @Validated
    data class ChannelProperties(
        @field:NotEmpty
        var name: String? = null,
        var transport: String? = null,
        var messageTypes: List<String> = listOf(),
        var receiverConfig: ReceiverProperties = ReceiverProperties(),
        var senderConfig: SenderProperties = SenderProperties()
    )

    @Validated
    data class TransportProperties(
        @field:NotEmpty
        var name: String? = null,
        @field:NotEmpty
        var broker: String? = null,
        var options: Map<String, String>? = null,
        var senderConfig: Map<String, String>? = null,
        var receiverConfig: Map<String, String>? = null,
    )

    @Validated
    data class ReceiverProperties(
        var concurrency: Int = 1,
    )

    @Validated
    data class SenderProperties(
        var acks: Int = 0,
    )
}
