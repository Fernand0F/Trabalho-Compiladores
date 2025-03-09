package token.attr

class CharLiteralAttr(val c: Char): LiteralAttr(DataType.CHAR) {
    override fun toString() = "$type, $c"
}
