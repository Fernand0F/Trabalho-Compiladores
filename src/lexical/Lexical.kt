package lexical

import Globals
import lexical.dfa.DFA
import token.Token

class Lexical<T>(private val dfa: DFA<T>) {
    private val tokenRules = mutableMapOf<T, ((String) -> Token)?>()
    private var fileReader: FileReader? = null
    private var columnCount = 0
    private var lineCount = 0

    fun setInput(filepath: String) = apply {
        fileReader = FileReader(filepath)
    }

    fun tokenRule(state: T, getToken: ((String) -> Token)?) = apply { tokenRules[state] = getToken }

    fun nextToken(): Token? {
        checkNotNull(fileReader) { "File input must be defined" }

        // Look for file end
        if (fileReader!!.getChar() == null)
            return null

        var lexeme = ""
        var state = dfa.reset().getState()

        while(!state.isAcceptState) {
            val c = fileReader!!.getChar() ?: Globals.EOF_CHAR

            state = dfa.put(c)

            if (!state.isLookAheadState) {
                lexeme += c
                fileReader!!.next()
                processRowColumn(c)
            }

            if (state.isErrorState)
                throw Exception("Malformed Token: $lexeme [line: $lineCount, column: $columnCount]")
        }

        // All token rules must be explicitly defined
        if (!tokenRules.containsKey(state.state))
            throw Exception("Token rule not set for ${state.state}")

        // If token rule is NULL throw the token away and get the next
        return tokenRules[state.state]?.invoke(lexeme) ?: nextToken()
    }

    private fun processRowColumn(c: Char) {
        if (c == '\n') {
            lineCount++
            columnCount = 0
        } else {
            columnCount++
        }
    }
}
