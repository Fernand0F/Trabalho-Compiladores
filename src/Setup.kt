import lexical.Lexical
import lexical.dfa.DFABuilder
import token.attr.IntConstAttr

object Setup {
    fun getDfa() = DFABuilder<Int>()
        .initialState(0)

        // if
        .transition(0, 'i', 1)
        .transition(1, 'f', 2)
        .accept(2)

        // while
        .transition(0, 'w', 3)
        .transition(3, 'h', 4)
        .transition(4, 'i', 5)
        .transition(5, 'l', 6)
        .transition(6, 'e', 7)
        .accept(7)

        // ws
        .transition(0, listOf('\t', '\n', '\r', ' '), 8)
        .transition(8, listOf('\t', '\n', '\r', ' '), 8)
        .lookAhead(8, 9)

        // Int
        .allDigits(0, 10)
        .allDigits(10, 10)
        .lookAhead(10, 11)

        .compile()

    fun getLex() = Lexical(getDfa())
        .tokenRule(2, "IF")
        .tokenRule(7, "WHILE")
        .tokenRule(11, "INT_CONST") { IntConstAttr(it.toInt()) }
}