package coroutines

import entities.Author
import entities.Book
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import kotlin.concurrent.thread

object Display {

    private val scope = CoroutineScope(CoroutineName("my scope") + Dispatchers.Default)

    private val infoArea = JTextArea().apply {
        isEditable = false
    }

    private val loadButton = JButton("Load button").apply {
        addActionListener {
            isEnabled = false
            infoArea.text = "Loading book...\n"
            scope.launch {
                val book = loadBook()
                infoArea.append("Book: ${book.title}, ${book.year}, ${book.genre}\n")
                infoArea.append("Loading author...\n")
                val author = loadAuthor(book)
                infoArea.append("Author: ${author.name}, ${author.bio}")
                isEnabled = true
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

    private suspend fun loadAuthor(book: Book): Author {
        longOperation()
        return Author("George Orwell", "British writer and journalist!")
    }

    fun startTimer() {
        thread {
            var totalSeconds = 0
            while (true) {
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                timer.text = String.format("Timer: %02d:%02d", minutes, seconds)
                Thread.sleep(1000)
                totalSeconds++
            }
        }
    }
}