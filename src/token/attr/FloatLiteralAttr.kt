package token.attr

class FloatLiteralAttr(val x: Float): LiteralAttr(DataType.FLOAT) {
    override fun toString() = "type=$type, value=$x"
}