import lexical.Lexical
import parser.ParseTreeNode
import parser.Parser
import symboltable.SymbolTable

fun main() {
    val symbolTable = SymbolTable()
    val lex = Lexical(symbolTable)
        .setInput("src/input.txt")

    val parser = Parser(lex)
    val tree = parser.parse()
    printParseTree(tree, "")
}

fun printParseTree(node: ParseTreeNode, indent: String, last: Boolean = true) {
    if (last) println("$indent└─ ${node.symbol}")
    else println("$indent├─ ${node.symbol}")
    var newIndent: String
    if (last) newIndent = indent + "   "
    else newIndent = indent + "│  "
    for (i in 0 until node.children.size) {
        printParseTree(node.children[i], newIndent, i == node.children.size - 1)
    }
}