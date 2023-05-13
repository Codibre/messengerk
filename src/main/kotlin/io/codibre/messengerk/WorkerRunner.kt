package io.codibre.messengerk

import kotlinx.coroutines.*
import io.codibre.messengerk.contracts.MessageBus
import io.codibre.messengerk.stamp.ReceivedStamp

class WorkerRunner(private val receivers: List<ReceiverWorker>, val bus: MessageBus) {

    private val jobSupervisor = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + jobSupervisor)
    private val workerPrefix = "receiver-worker"

    suspend fun start() {
        var i = 0
        receivers.forEach { receiver ->
            i++
            scope.launch(CoroutineName("$workerPrefix-$i")) {
                while (isActive) {
                    try {
                        val envelopes = receiver.receive()
                        envelopes.forEach { envelope ->
                            bus.dispatch(envelope.with(ReceivedStamp(receiver.metadata()["transport"]!!)))
                        }
                        receiver.ack()
                    } catch (e: CancellationException) {
                        receiver.ack() // ack the last message
                        receiver.stop()
                        throw e
                    } catch (e: Throwable) {
                        receiver.stop()
                        throw e
                        // todo: Mechanism for regenerating worker
                    }
                }
            }
        }
    }

    suspend fun stop() {
        runBlocking {
            jobSupervisor.cancelAndJoin()
        }
    }
}
