package token.attr

class IntLiteralAttr(val n: Int): LiteralAttr(DataType.INT) {
    override fun toString() = "$type, $n"
}