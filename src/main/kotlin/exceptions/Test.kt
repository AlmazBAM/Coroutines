package exceptions

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    println("Catched exception ${throwable.message}")
}
private val scope = CoroutineScope(dispatcher + exceptionHandler)

fun main() {

    scope.launch() {
        async {
             method()
        }
    }

    scope.launch() {
        method2()
    }
}

suspend fun method(): String {
    delay(3000)
    error("Some error 1")
}

suspend fun method2() {
    delay(5000)
    println("Some error 2")
}