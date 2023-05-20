package com.github.codibre.messengerk.messengerk_spring_boot_starter


import com.github.codibre.messengerk.core.ReceiverWorker
import com.github.codibre.messengerk.core.WorkerRunner
import com.github.codibre.messengerk.core.contracts.MessageBus
import com.github.codibre.messengerk.core.transport.RouteRegistry
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
@Async
open class WorkerApplicationRunner : ApplicationRunner, ApplicationContextAware, DisposableBean {

    private lateinit var applicationContext: ApplicationContext
    private lateinit var workerRunner: WorkerRunner

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun run(args: ApplicationArguments) {
        val RouteRegistry = applicationContext.getBean("routeRegistry") as RouteRegistry

        val receivers: MutableMap<String, List<ReceiverWorker>> = mutableMapOf()

        for ((_, routeCallable) in RouteRegistry) {
            val route = routeCallable()
            route.transport.subscribe(route.channel)
            val transportReceivers = route.transport.getReceivers()

            if (transportReceivers.isNotEmpty()) {
                receivers.putIfAbsent(route.transport.name, transportReceivers)
            }
        }

        val bus = applicationContext.getBean("routableMessageBus") as MessageBus

        if (receivers.isNotEmpty()) {
            workerRunner = WorkerRunner(receivers.values.flatten(), bus)
            println("Starting workers in thread: ${Thread.currentThread().name}")

            runBlocking {
                workerRunner.start()
            }
        }
    }

    override fun destroy() {
        runBlocking {
            workerRunner.stop()
        }
    }
}
