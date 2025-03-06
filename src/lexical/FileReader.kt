package lexical

import java.io.File

class FileReader(filepath: String, private val bufferSize: Int = 100) {
    private val reader = File(filepath).bufferedReader()
    private var buffer = CharArray(0)
    private var bufferIndex = 0
    private var closed = false

    fun getChar(): Char? {
        if (closed || (bufferIndex == buffer.size && !nextChunk()))
            return null
        return buffer[bufferIndex]
    }

    fun next() {
        bufferIndex++
    }

    private fun nextChunk(): Boolean {
        buffer = CharArray(bufferSize)

        val bytesRead = reader.read(buffer)

        if (bytesRead == -1) {
            reader.close()
            closed = true
            return false
        }

        buffer = buffer.copyOf(bytesRead)
        bufferIndex = 0

        return true
    }
}
