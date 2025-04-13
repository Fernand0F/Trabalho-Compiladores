package parser

import token.TokenName

class SymbolName {
    val isTerminal: Boolean
    var nonTerminal: NonTerminal? = null
    var terminal: TokenName? = null

    constructor(nonTerminal: NonTerminal) {
        this.nonTerminal = nonTerminal
        isTerminal = false
    }
    constructor(terminal: TokenName) {
        this.terminal = terminal
        isTerminal = true
    }
}