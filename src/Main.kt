import lexical.Lexical
import symboltable.SymbolTable

fun main() {
    val symbolTable = SymbolTable()
    val lex = Lexical(symbolTable)
        .setInput("src/input.txt")

    do {
        val token = lex.nextToken()
        println(token)
    } while(token != null)

    println("\nTabela de Símbolos")
    println(symbolTable)
}