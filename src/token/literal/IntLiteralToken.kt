package token.literal

class IntLiteralToken(val n: Int): LiteralToken(DataType.INT) {
    override fun toString() = "[$name: $type, $n]"
}