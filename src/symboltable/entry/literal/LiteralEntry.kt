package symboltable.entry.literal

import symboltable.entry.TableEntry
import token.TokenName
import token.attr.DataType

abstract class LiteralEntry(token: TokenName, lexeme: String, val type: DataType): TableEntry(token, lexeme) {
    abstract override fun toString(): String
}