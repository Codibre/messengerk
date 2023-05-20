package com.github.codibre.messengerk.kafka_transport_spring_boot_starter
import com.github.codibre.messengerk.kafka_transport.KafkaTransportFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Autoconfiguration class for kafka transport
 *
 * @author Gustavo Andrade Ferreira <jojovembh@gmail.com>
 */
@Configuration
@ConditionalOnClass(KafkaTransportFactory::class)
open class KafkaTransportAutoConfiguration {
    @Bean
    open fun kafkaTransport(): KafkaTransportFactory = KafkaTransportFactory()
}
