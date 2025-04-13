package parser

import token.Token

class Symbol {
    val isTerminal: Boolean
    var nonTerminal: NonTerminal? = null
    var terminal: Token? = null

    constructor(nonTerminal: NonTerminal) {
        this.nonTerminal = nonTerminal
        isTerminal = false
    }
    constructor(terminal: Token) {
        this.terminal = terminal
        isTerminal = true
    }

    override fun toString(): String {
        if (isTerminal) return terminal.toString()
        return nonTerminal.toString()
    }
}