package token.attr

class IntConstAttr(val n: Int): TypeAttr(DataType.INT) {
    override fun toString() = "$type, $n"
}