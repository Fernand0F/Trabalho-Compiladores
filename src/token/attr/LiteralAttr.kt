package token.attr

open class LiteralAttr(val type: DataType): TokenAttr {
    override fun toString() = "$type"
}