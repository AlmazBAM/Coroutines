package executors

import java.util.concurrent.Executors

fun main() {
    val executor = Executors.newFixedThreadPool(1000)
//    val executor = Executors.newSingleThreadExecutor(1000)
//    val executor = Executors.newCachedThreadPool()

    repeat(10_000) {
        executor.execute {
            val image = Image(it)
            processImage(image = image)
        }
    }
}

private fun processImage(image: Image) {
    println("Image $image is processing")
    Thread.sleep(1000)
    println("Image $image processed")
}

data class Image(val id: Int)