package parser

import lexical.Lexical
import token.Token
import token.TokenName
import java.util.Stack

class Parser(val lexer: Lexical) {
    val productions: MutableList<MutableList<SymbolName>> = mutableListOf(
        //arrayListOf(Symbol(TokenName.PROGRAM), Symbol(TokenName.ID), Symbol(TokenName.LPAR), Symbol(TokenName.RPAR), Symbol(NonTerminal.BLOCO)),
        //arrayListOf(Symbol(TokenName.BLOCKOPEN), Symbol(NonTerminal.DECLARACOES), Symbol(NonTerminal.COMANDOS), Symbol(TokenName.BLOCKCLOSE)),
        //arrayListOf(Symbol(NonTerminal.DECLARACAO), Symbol(NonTerminal.DECLARACOES)),
        //arrayListOf(Symbol(TokenName.EPSILON))
        mutableListOf(SymbolName(TokenName.IF), SymbolName(NonTerminal.EXPRESSAO), SymbolName(TokenName.THEN), SymbolName(NonTerminal.PROGRAMA), SymbolName(NonTerminal.BLOCO)),
        mutableListOf(SymbolName(TokenName.ASSIGN)),
        mutableListOf(SymbolName(TokenName.ELSE), SymbolName(NonTerminal.PROGRAMA)),
        mutableListOf(SymbolName(TokenName.EPSILON)),
        mutableListOf(SymbolName(TokenName.CONST_NUM))
    )
    val table: Map<Pair<NonTerminal, TokenName>, Int> = mapOf(
        Pair(NonTerminal.PROGRAMA, TokenName.IF) to 0,
        Pair(NonTerminal.PROGRAMA, TokenName.ASSIGN) to 1,
        Pair(NonTerminal.BLOCO, TokenName.ELSE) to 2,
        Pair(NonTerminal.BLOCO, TokenName.EOF) to 3,
        Pair(NonTerminal.EXPRESSAO, TokenName.CONST_NUM) to 4,
    )

    fun parse(): ParseTreeNode {
        val stack = Stack<SymbolName>()
        stack.push(SymbolName(NonTerminal.PROGRAMA))
        var nextToken = lexer.nextToken()
        val nodeStack = Stack<ParseTreeNode>()
        val root = ParseTreeNode(Symbol(NonTerminal.PROGRAMA))
        nodeStack.push(root)

        while (!stack.isEmpty()) {
            val symbol = stack.peek()
            val currentNode = nodeStack.pop()
            if (symbol.isTerminal) {
                if (symbol.terminal!! == nextToken.name) {
                    stack.pop()
                    currentNode.symbol.terminal!!.loc = nextToken.loc
                    currentNode.symbol.terminal!!.attr = nextToken.attr
                    nextToken = lexer.nextToken()
                }
                else if (symbol.terminal!! == TokenName.EPSILON) {
                    stack.pop()
                    currentNode.symbol.terminal!!.loc = nextToken.loc
                    currentNode.symbol.terminal!!.attr = nextToken.attr
                }
                else {
                    throw Exception("Erro")
                }
            }
            else {
                val production = table.getOrDefault(Pair(symbol.nonTerminal!!, nextToken.name), -1)
                if (production == -1) {
                    throw Exception("Erro")
                }
                else {
                    stack.pop()
                    for (y in productions[production].asReversed()) {
                        stack.push(y)
                        val node = when (y.isTerminal) {
                            true -> ParseTreeNode(Symbol(Token(y.terminal!!, Pair(0,0), null)))
                            false -> ParseTreeNode(Symbol(y.nonTerminal!!))
                        }
                        nodeStack.push(node)
                        currentNode.children.add(node)
                    }
                    currentNode.children.reverse()
                }
            }
        }
        if (nextToken.name != TokenName.EOF) {
            throw Exception("Erro")
        }
        return root
    }
}