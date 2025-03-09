package token.attr

class TypeAttr(val type: DataType): TokenAttr {
    override fun toString() = "type=$type"
}