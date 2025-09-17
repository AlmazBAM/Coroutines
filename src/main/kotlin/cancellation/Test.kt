package cancellation

import kotlinx.coroutines.*
import java.util.concurrent.Executors

private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

fun main() {
    val job =  scope.launch {
        timer()

    }
    Thread.sleep(4000)
    job.cancel()
}

private suspend fun timer() {
    var seconds = 0
    while (true) {
        try {
            currentCoroutineContext().ensureActive()
            println(seconds++)
            delay(1000)
        } catch (e: CancellationException) {
            throw e
        }
        catch (e: Exception) {
        }
    }
}