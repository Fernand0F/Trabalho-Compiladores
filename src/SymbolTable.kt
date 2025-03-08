class SymbolTable {
    private val table = HashMap<String, Int>()
    private var i = 0

    fun get(symbol: String) = table.getOrPut(symbol) { i++ }

    fun toArray() = table.toList().fold(Array(i) { "" }) { carry, (symbol, i) ->
        carry[i] = symbol
        return carry
    }

    override fun toString() = buildString {
        toArray().forEachIndexed { i, symbol ->
            append("${i.toString().padStart(3, '0')}: $symbol\n")
        }
    }
}