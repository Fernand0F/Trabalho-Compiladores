package symboltable.entry.literal

import token.TokenName
import token.attr.DataType

class IntLiteralEntry(lexeme: String, val value: Int): LiteralEntry(TokenName.CONST_NUM, lexeme, DataType.INT) {
    override fun toString() = buildString {
        append(token.toString().padEnd(padSize[0]))
        append(" | ")
        append(lexeme.padEnd(padSize[1]))
        append(" | ")
        append(type.toString().padEnd(padSize[2]))
        append(" | ")
        append(value.toString().padEnd(padSize[3]))
    }
}