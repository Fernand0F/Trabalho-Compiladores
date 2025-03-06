package token.attr

open class TypeAttr(val type: DataType): TokenAttr {
    override fun toString() = "$type"
}