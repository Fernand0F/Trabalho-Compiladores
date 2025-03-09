package symboltable

import symboltable.entry.TableEntry

class SymbolTable {
    private val table = HashMap<String, Pair<Int, TableEntry>>()
    private var i = 0

    fun get(entry: TableEntry) = table.getOrPut(entry.lexeme) { Pair(i++, entry) }.first

    fun build(): Array<TableEntry?> {
        val a = Array<TableEntry?>(i) { null }
        for ((index, entry) in table.values)
            a[index] = entry
        return a
    }

    override fun toString() = buildString {
        for ((i, entry) in build().withIndex())
            append("${i.toString().padStart(3, '0')}: $entry\n")
    }
}