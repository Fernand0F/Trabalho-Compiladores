package parser

class ParseTreeNode(val symbol: Symbol, val children: MutableList<ParseTreeNode> = mutableListOf()) {
    override fun toString(): String {
        return symbol.toString()
    }
}