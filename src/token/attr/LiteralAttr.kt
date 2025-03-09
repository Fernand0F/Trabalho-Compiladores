package token.attr

abstract class LiteralAttr(val type: DataType): TokenAttr {
    abstract override fun toString(): String
}