package com.github.codibre.messengerk.spring_boot_starter


import com.github.codibre.messengerk.core.ChannelRegistry
import com.github.codibre.messengerk.core.Envelope
import com.github.codibre.messengerk.core.annotations.MessageHandler
import com.github.codibre.messengerk.core.contracts.MessageBus
import com.github.codibre.messengerk.core.transport.RouteRegistry
import com.github.codibre.messengerk.core.transport.TransportRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.mock.env.MockEnvironment
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.reflect.Field

@SpringBootTest(classes = [MessengerAutoConfiguration::class])
@ExtendWith(SpringExtension::class)
class MessengerAutoConfigurationTest {
    @Mock
    private lateinit var applicationContext: ApplicationContext

    @Mock
    private lateinit var beanFactory: ConfigurableListableBeanFactory

    @Mock
    private lateinit var environment: Environment

    private lateinit var messengerAutoConfiguration: MessengerAutoConfiguration

    private lateinit var applicationContextRunner: ApplicationContextRunner

    @BeforeEach
    fun setUp() {
        applicationContextRunner = ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MessengerAutoConfiguration::class.java))
    }

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Set expectation for applicationContext.autowireCapableBeanFactory
        `when`(applicationContext.autowireCapableBeanFactory).thenReturn(beanFactory)

        messengerAutoConfiguration = MessengerAutoConfiguration()
        messengerAutoConfiguration.setApplicationContext(applicationContext)

        environment = MockEnvironment()

        applicationContextRunner = ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MessengerAutoConfiguration::class.java))
    }

    @Test
    fun `setApplicationContext should set the application context and bean factory`() {
        // Given
        val applicationContextAware = messengerAutoConfiguration as ApplicationContextAware

        // When
        applicationContextAware.setApplicationContext(applicationContext)

        // Then
        // Assert that the application context and bean factory are set correctly
        val applicationContextField: Field = MessengerAutoConfiguration::class.java.getDeclaredField("context")
        applicationContextField.isAccessible = true
        val contextValue = applicationContextField.get(messengerAutoConfiguration)
        assertSame(applicationContext, contextValue)

        val beanFactoryField: Field = MessengerAutoConfiguration::class.java.getDeclaredField("beanFactory")
        beanFactoryField.isAccessible = true
        val beanFactoryValue = beanFactoryField.get(messengerAutoConfiguration)
        assertSame(beanFactory, beanFactoryValue)
    }

    @Test
    fun `setEnvironment should set the environment and bind properties`() {
        // Given
        val environmentAware = messengerAutoConfiguration as EnvironmentAware

        // When
        environmentAware.setEnvironment(environment)

        // Then
        // Assert that the environment is set correctly and properties are bound
        val propertiesField: Field = MessengerAutoConfiguration::class.java.getDeclaredField("properties")
        propertiesField.isAccessible = true
        val propertiesValue = propertiesField.get(messengerAutoConfiguration)
        assertNotNull(propertiesValue)

        val validatorField: Field = MessengerAutoConfiguration::class.java.getDeclaredField("validator")
        validatorField.isAccessible = true
        val validatorValue = validatorField.get(messengerAutoConfiguration)
        assertNotNull(validatorValue)
    }

    @Test
    fun `test it has a default bus`() {
        applicationContextRunner.run {
            assertThat(it).hasBean("default.bus")
            assertThat(it).getBean("default.bus") is MessageBus
        }
    }

    @Test
    fun `test empty bus bean`() {
        applicationContextRunner.withPropertyValues(
            "messenger.buses[0].name=bus1"
        ).run {
            assertThat(it).hasBean("bus1")
            assertThat(it).getBean("bus1") is MessageBus
            val bus1 = it.getBean("bus1")
            val bus2 = it.getBean("bus1")
            assertTrue(bus1 === bus2) // guarantee is singleton

            // assert that default.bus exists regardless of custom buses
            assertThat(it).hasBean("default.bus")
            assertThat(it).getBean("default.bus") is MessageBus
        }
    }

    // TODO: review the package name scanner
//    @Test
//    fun `bus bean with a handler`() {
//        applicationContextRunner.withPropertyValues("messenger.buses[0].name=bus1")
//            .withBean(FooHandler::class.java)
//            .run {
//                val bus = it.getBean("bus1") as MessageBus
//                val result = bus.dispatch(FooMessage("test")).getOrThrow() as Envelope<*>
//                val handledStamp = result.firstOf<HandledStamp>() as HandledStamp
//                assertNotNull(handledStamp)
//                assertEquals(handledStamp.result, "Foo")
//            }
//    }

    @Test
    fun `bus bean with a channel`() {
        applicationContextRunner.withPropertyValues(
            "messenger.channels[0].name=channel1"
        ).run {
            assertThat(it).hasBean("channelRegistry")
            val channelRegistry = it.getBean("channelRegistry") as ChannelRegistry
            assertNotNull(channelRegistry["channel1"])
        }
    }

    @Test
    fun `bus bean with a transport with valid broker`() {
        applicationContextRunner.withPropertyValues(
            "messenger.transports[0].name=transport1",
            "messenger.transports[0].broker=sync"
        ).run {
            assertThat(it).hasBean("transportRegistry")
            val transportRegistry = it.getBean("transportRegistry") as TransportRegistry
            assertNotNull(transportRegistry["transport1"])
        }
    }

    @Test
    fun `bus bean with a transport with invalid broker`() {
        assertThrows<Throwable> {
            applicationContextRunner.withPropertyValues(
                "messenger.transports[0].name=transport1",
                "messenger.transports[0].broker=test"
            ).run {
                assertThat(it).hasBean("transportRegistry")
                val transportRegistry = it.getBean("transportRegistry") as ChannelRegistry
                assertNotNull(transportRegistry["transport1"])
            }
        }
    }

    @Test
    fun `bus bean with with route`() {
        applicationContextRunner.withPropertyValues(
            "messenger.transports[0].name=transport1",
            "messenger.transports[0].broker=sync",
            "messenger.channels[0].name=channel1",
            "messenger.channels[0].transport=transport1",
            "messenger.channels[0].messageTypes[0]=foo",
        ).run {
            assertThat(it).hasBean("routeRegistry")
            val routeRegistry = it.getBean("routeRegistry") as RouteRegistry
            assertNotNull(routeRegistry["foo"])
            val route = routeRegistry["foo"]!!.invoke()
            assertTrue(route.channel.name == "channel1")
            assertTrue(route.transport.name == "transport1")
        }
    }

    @Test
    fun `bus bean with with invalid route`() {
        applicationContextRunner.withPropertyValues(
            "messenger.transports[0].name=transport1",
            "messenger.transports[0].broker=sync",
            "messenger.channels[0].name=channel1",
            "messenger.channels[0].transport=transport2",
            "messenger.channels[0].messageTypes[0]=foo",
        ).run {

        }
    }
}

data class FooMessage(val name: String)

class FooHandler {
    @MessageHandler
    fun fooHandler(envelope: Envelope<FooMessage>): String {
        return "Foo"
    }
}
