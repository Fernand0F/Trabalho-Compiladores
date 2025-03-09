class SymbolTable {
    private val table = HashMap<String, Int>()
    private var i = 0

    fun get(symbol: String) = table.getOrPut(symbol) { i++ }

    fun toArray(): Array<String> {
        val a = Array(i) { "" }
        for ((symbol, index) in table)
            a[index] = symbol
        return a
    }

    override fun toString() = buildString {
        for ((i, symbol) in toArray().withIndex())
            append("${i.toString().padStart(3, '0')}: $symbol\n")
    }
}