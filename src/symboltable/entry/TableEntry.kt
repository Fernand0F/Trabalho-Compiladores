package symboltable.entry

import token.TokenName

open class TableEntry(val token: TokenName, val lexeme: String) {
    protected val padSize = arrayOf(10, 20, 5, 10)

    override fun toString() = buildString {
        append(token.toString().padEnd(padSize[0]))
        append(" | ")
        append(lexeme.padEnd(padSize[1]))
    }
}