package suspenFromCoroutines

import entities.Author
import entities.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.awt.Dimension
import java.util.concurrent.Executors
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Display {

    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    private val scope = CoroutineScope(dispatcher)


    private val infoArea = JTextArea().apply {
        isEditable = false
    }

    private val loadButton = JButton("Load button").apply {
        addActionListener {
            scope.launch {
                isEnabled = false
                infoArea.text = "Loading book...\n"
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
    }

    fun show() {
        mainFrame.isVisible = true
        startTimer()
    }

    private suspend fun loadBook(): Book {
        return suspendCoroutine { continuation ->
            loadBook { book ->
                continuation.resumeWith(Result.success(book))
            }
        }
    }

    private fun loadBook(callback: (Book) -> Unit) {
        thread {
            Thread.sleep(3000)
            callback(
                Book("1983", 1945, "Dystopia")
            )
        }
    }

    private suspend fun loadAuthor(book: Book): Author {
        return suspendCoroutine { continuation ->
            loadAuthor(book) { author ->
                continuation.resumeWith(Result.success(author))
            }
        }
    }

    private fun loadAuthor(book: Book, callback: (Author) -> Unit) {
        thread {
            Thread.sleep(3000)
            callback(Author("George Orwell", "British writer and journalist!"))
        }
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