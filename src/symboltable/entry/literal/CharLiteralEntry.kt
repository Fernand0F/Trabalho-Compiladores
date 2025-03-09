package symboltable.entry.literal

import token.TokenName
import token.attr.DataType

class CharLiteralEntry(lexeme: String, val value: Char): LiteralEntry(TokenName.CONST_CHAR, lexeme, DataType.CHAR) {
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