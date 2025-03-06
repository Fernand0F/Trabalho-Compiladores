package lexical

import Globals
import lexical.dfa.DFA
import token.Token
import token.attr.TokenAttr

class Lexical<T>(private val dfa: DFA<T>) {
    private val tokenRules = mutableMapOf<T, Pair<String, (String) -> TokenAttr?>>()
    private var fileReader: FileReader? = null
    private var rowCount = 1
    private var columnCount = 1

    fun setInput(filepath: String) = apply {
        fileReader = FileReader(filepath)
    }

    fun tokenRule(
        state: T,
        tokenName: String,
        getToken: (String) -> TokenAttr? = { null }
    ) = apply {
        tokenRules[state] = Pair(tokenName, getToken)
    }

    fun nextToken(): Token? {
        checkNotNull(fileReader) { "File input must be defined" }

        // Look for file end
        if (fileReader!!.getChar() == null)
            return null

        var lexeme = ""
        var state = dfa.reset().getState()
        val lexemeStart = Pair(rowCount, columnCount)

        while(!state.isAcceptState) {
            val c = fileReader!!.getChar() ?: Globals.EOF_CHAR

            state = dfa.put(c)

            if (!state.isLookAheadState) {
                lexeme += c
                fileReader!!.next()
                processRowColumn(c)
            }

            if (state.isErrorState)
                throw Exception("Malformed Token: \"$lexeme\"[row: ${lexemeStart.first}, column: ${lexemeStart.second}]")
        }

        return tokenRules[state.state]?.let {
            Token(it.first, lexemeStart, it.second(lexeme))
        } ?: nextToken()
    }

    private fun processRowColumn(c: Char) {
        if (c == '\n') {
            rowCount++
            columnCount = 1
        } else {
            columnCount++
        }
    }
}
