package com.github.codibre.messengerk.messengerk_spring_boot_starter


import com.github.codibre.messengerk.core.*
import com.github.codibre.messengerk.core.contracts.MessageBus
import com.github.codibre.messengerk.core.handler.HandlerDescriptor
import com.github.codibre.messengerk.core.transport.*
import com.github.codibre.messengerk.core.transport.memory.InMemoryTransportFactory
import com.github.codibre.messengerk.core.transport.sync.SyncTransportFactory
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import kotlin.collections.set


/**
 * Autoconfiguration class for Messenger library
 *
 * This class has the goal of starting up the beans necessary for spring boot
 *
 * @author Gustavo Andrade Ferreira <jojovembh@gmail.com>
 */
@Configuration
@EnableAsync
@EnableConfigurationProperties(MessengerProperties::class)
open class MessengerAutoConfiguration : BeanDefinitionRegistryPostProcessor, ApplicationContextAware, EnvironmentAware {
    private var properties: MessengerProperties? = null
    private var context: ApplicationContext? = null
    private var validator: Validator? = null
    private lateinit var beanFactory: ConfigurableListableBeanFactory

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
        beanFactory = applicationContext.autowireCapableBeanFactory as ConfigurableListableBeanFactory
    }

    @Bean
    open fun validator(): Validator {
        return LocalValidatorFactoryBean()
    }

    override fun setEnvironment(environment: Environment) {
        validator = Validation.buildDefaultValidatorFactory().validator
        bindProperties(environment)
    }

    private fun bindProperties(environment: Environment) {
        val binder = Binder.get(environment)
        val properties = binder.bind("messenger", MessengerProperties::class.java).orElse(MessengerProperties())

        // validate the properties object
        val violations = validator?.validate(properties)
        if (!violations.isNullOrEmpty()) {
            throw MessengerConfigValidationException(ConstraintViolationException(violations))
        }

        this.properties = properties
    }

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
    }

    override fun postProcessBeanFactory(context: ConfigurableListableBeanFactory) {
        boot()
    }

    private fun boot() {
        val handlerClasses: MutableMap<String, MutableList<HandlerDescriptor>> =
            extractHandlers(getCallingApplicationBasePackage())
        properties?.buses?.forEach { busProperties ->
            val bus = buildBus(busProperties, handlerClasses)
            registerBusSingleton(bus)
        }

        val defaultBus = buildBus(MessengerProperties.BusProperties(name = "default.bus"), handlerClasses)
        registerBusSingleton(defaultBus)
    }

    /**
     * Retrieves the base package of the calling application.
     * It uses the last stack trace element as the indicator of the calling application.
     * Assumes that this method is invoked directly or indirectly by the calling application.
     *
     * @return The base package of the calling application, or an empty string if not found.
     */
    private fun getCallingApplicationBasePackage(): String {
        val stackTrace = Thread.currentThread().stackTrace
        val callingApplicationElement = stackTrace.lastOrNull()
        val callingApplicationClassName = callingApplicationElement?.className ?: ""
        val lastDotIndex = callingApplicationClassName.lastIndexOf('.')
        return if (lastDotIndex >= 0) {
            callingApplicationClassName.substring(0, lastDotIndex)
        } else {
            ""
        }
    }

    private fun registerBusSingleton(bus: MessageBus) {
        beanFactory.registerSingleton(bus.name, bus)
    }

    @Bean
    open fun channelRegistry(): ChannelRegistry {
        val channelRegistry = ChannelRegistry()

        properties?.channels?.forEach { channelProperties ->
            val name = channelProperties.name
            val receiverConfig = channelProperties.receiverConfig

            if (name != null) {
                val channel = createChannel(name, channelProperties)

                channelRegistry[name] = { channel }
            }
        }

        return channelRegistry
    }

    private fun createChannel(name: String, channelProperties: MessengerProperties.ChannelProperties): Channel {
        val receiverConfig = channelProperties.receiverConfig
        val senderConfig = channelProperties.senderConfig

        val receiverConcurrency = receiverConfig.concurrency

        return Channel(
            name,
            channelProperties.messageTypes,
            ReceiverConfig(receiverConcurrency),
            SenderConfig()
        )
    }

    @Bean
    open fun messageBusRegistry(): MessageBusRegistry {
        val registry = MessageBusRegistry()

        properties?.buses?.forEach { busProperty ->
            val busName = busProperty.name
            if (busName != null) {
                registry[busName] = { context?.getBean(busName) as MessageBus }
            }
        }

        registry["default.bus"] = { context?.getBean("default.bus") as MessageBus }

        return registry
    }

    private fun extractHandlers(basePackage: String): MutableMap<String, MutableList<HandlerDescriptor>> {
        val handlers: MutableMap<String, MutableList<HandlerDescriptor>> = mutableMapOf()
        val handlerFinder = AnnotationFinder()
        val annotatedHandlersMap = handlerFinder.findHandlers(
            basePackage,
            context!!
        )

        annotatedHandlersMap.forEach { (key, value) ->
            handlers.computeIfAbsent(key) { mutableListOf() }.addAll(value)
        }

        return handlers
    }

    private fun buildBus(
        busProperties: MessengerProperties.BusProperties,
        handlersMap: MutableMap<String, MutableList<HandlerDescriptor>>
    ): MessageBus {
        val busName = busProperties.name ?: throw IllegalArgumentException("Bus name cannot be null")
        val routeRegistry = beanFactory.getBean("routeRegistry") as RouteRegistry
        val transportRegistry = beanFactory.getBean("transportRegistry") as TransportRegistry

        val bus = MessageBusBuilder(busName).build {
            handlersMap.forEach { (_, handlers) ->
                withHandlers(handlers)
            }

            busProperties.middlewares?.forEach { middleware ->
                withMiddleware(middleware)
            }

            withRouteRegistry(routeRegistry)
            withTransportRegistry(transportRegistry)
            allowNoHandlers(busProperties.allowNoHandler)
        }

        return bus
    }

    /**
     * Registers the transports
     *
     * @return TransportRegistry
     */
    @Bean
    open fun transportRegistry(): TransportRegistry {
        val transportRegistry = TransportRegistry()

        @Suppress("UNCHECKED_CAST")
        val factories = context?.getBean("transportFactories") as List<TransportFactory>

        properties?.transports?.forEach { transportProperties ->
            if (transportRegistry.containsKey(transportProperties.name)) {
                return@forEach
            }

            val transportFactory = factories.firstOrNull { factory ->
                transportProperties.broker?.let { factory.supports(it) } ?: false
            } ?: throw NoSuchElementException("No matching factory found for broker: ${transportProperties.broker}")

            val transportConfig = TransportConfig(
                options = transportProperties.options ?: mapOf(),
                receiverConfig = transportProperties.receiverConfig ?: mapOf(),
                senderConfig = transportProperties.senderConfig ?: mapOf()
            )

            val transportName = transportProperties.name

            transportName?.let { name ->
                val transport = transportFactory.create(name, transportConfig)
                transportRegistry[name] = { transport }
            }
        }

        return transportRegistry
    }

    @Bean
    open fun routeRegistry(): RouteRegistry {
        val transportRegistry = context?.getBean("transportRegistry") as TransportRegistry
        val channelRegistry = context?.getBean("channelRegistry") as ChannelRegistry
        val routeRegistry = RouteRegistry()

        properties?.transports?.forEach { transportProperties ->
            properties?.channels?.forEach { channelProperties ->
                if (channelProperties.transport == transportProperties.name) {
                    val transportLocator = transportRegistry[transportProperties.name]
                    val channelLocator = channelRegistry[channelProperties.name]

                    if (transportLocator == null || channelLocator == null) {
                        throw Exception("Channel or transport not found")
                    }

                    channelProperties.messageTypes.forEach { messageType ->
                        routeRegistry[messageType] = createRoute(channelLocator, transportLocator)
                    }
                }
            }
        }

        transportRegistry["sync-transport"]?.let { syncTransport ->
            routeRegistry["default"] = createDefaultRoute(syncTransport)
        }

        return routeRegistry
    }

    private fun createRoute(channelLocator: () -> Channel, transportLocator: () -> Transport): () -> Route {
        return {
            Route(channelLocator.invoke(), transportLocator.invoke())
        }
    }

    private fun createDefaultRoute(syncTransport: () -> Transport): () -> Route {
        return {
            Route(Channel("default"), syncTransport.invoke())
        }
    }

    @Bean
    open fun inMemoryFactory(): InMemoryTransportFactory = InMemoryTransportFactory()

    @Bean
    open fun syncTransportFactory(routableBus: RoutableMessageBus): SyncTransportFactory =
        SyncTransportFactory { routableBus }

    @Bean
    @Primary
    open fun routableMessageBus(busRegistry: MessageBusRegistry): RoutableMessageBus =
        RoutableMessageBus(busRegistry)

    @Bean
    open fun transportFactories(): List<TransportFactory> {

        val transportFactories = context?.getBeansOfType(TransportFactory::class.java)

        return listOf(
            InMemoryTransportFactory(),
            context?.getBean("syncTransportFactory") as SyncTransportFactory
        )
    }
}
