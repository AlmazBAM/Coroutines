package concurrency

import entities.Book
import kotlinx.coroutines.*
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

object Display {

//    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    val dispatcher = Dispatchers.IO.limitedParallelism(1)
    private val scope = CoroutineScope(CoroutineName("my scope") + dispatcher)

    private val infoArea = JTextArea().apply {
        isEditable = false
    }

    private val loadButton = JButton("Load button").apply {
        addActionListener {
            isEnabled = false
            infoArea.text = "Loading book...\n"
            val jobList = mutableListOf<Job>()
            repeat(10) {
                scope.launch {
                    val book = loadBook()
                    infoArea.append("Book $it: ${book.title}, ${book.year}, ${book.genre}\n\n")
                }.also { job -> jobList.add(job) }

                scope.launch {
                    jobList.joinAll()
                    isEnabled = true
                }
            }
        }
    }

    private val timer = JLabel("TImer: 00:00")

    private val topPanel = JPanel(BorderLayout()).apply {
        add(timer, BorderLayout.WEST)
        add(loadButton, BorderLayout.EAST)

    }

    private val mainFrame = JFrame("Book and author info").apply {
        layout = BorderLayout()
        add(topPanel, BorderLayout.NORTH)
        add(JScrollPane(infoArea), BorderLayout.CENTER)
        size = Dimension(400, 300)
        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                scope.cancel()
            }
        })
    }

    fun show() {
        mainFrame.isVisible = true
        startTimer()
    }

    private suspend fun longOperation() {
        withContext(Dispatchers.Default) {
            mutableListOf<Int>().apply {
                repeat(300_000) {
                    add(0, it)
                }
            }
        }
    }

    private suspend fun loadBook(): Book {
        longOperation()
        return Book("1983", 1945, "Dystopia")

    }


    fun startTimer() {
        scope.launch {
            var totalSeconds = 0
            while (true) {
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                timer.text = String.format("Timer: %02d:%02d", minutes, seconds)
                delay(1000)
                totalSeconds++
            }
        }
    }
}