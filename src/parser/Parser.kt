package parser

import lexical.Lexical
import token.Token
import token.TokenName
import java.util.Stack

class Parser(val lexer: Lexical) {
    val productions: MutableList<MutableList<SymbolName>> = mutableListOf(
        mutableListOf(SymbolName(TokenName.PROGRAM), SymbolName(TokenName.ID), SymbolName(TokenName.LPAR), SymbolName(TokenName.RPAR), SymbolName(NonTerminal.BLOCO)),

        mutableListOf(SymbolName(TokenName.BLOCKOPEN), SymbolName(NonTerminal.DECLARACOES), SymbolName(NonTerminal.COMANDOS), SymbolName(TokenName.BLOCKCLOSE)),

        mutableListOf(SymbolName(NonTerminal.DECLARACAO), SymbolName(NonTerminal.DECLARACOES)),

        mutableListOf(SymbolName(TokenName.EPSILON)),

        mutableListOf(SymbolName(TokenName.TYPE), SymbolName(TokenName.COLON), SymbolName(NonTerminal.LISTA_IDS), SymbolName(TokenName.SEP)),

        mutableListOf(SymbolName(TokenName.ID), SymbolName(NonTerminal.LISTA_IDS2)),

        mutableListOf(SymbolName(TokenName.COMMA), SymbolName(NonTerminal.LISTA_IDS)),

        mutableListOf(SymbolName(TokenName.EPSILON)),

        mutableListOf(SymbolName(NonTerminal.COMANDO), SymbolName(NonTerminal.COMANDOS)),

        mutableListOf(SymbolName(TokenName.EPSILON)),

        mutableListOf(SymbolName(NonTerminal.SELECAO)),

        mutableListOf(SymbolName(NonTerminal.REPETICAO_WD)),

        mutableListOf(SymbolName(NonTerminal.REPETICAO_DW)),

        mutableListOf(SymbolName(NonTerminal.ATRIBUICAO)),

        mutableListOf(SymbolName(TokenName.IF), SymbolName(TokenName.LBRACKET), SymbolName(NonTerminal.CONDICAO), SymbolName(TokenName.RBRACKET), SymbolName(TokenName.THEN), SymbolName(NonTerminal.CMD_BLOCO), SymbolName(NonTerminal.ELSE_IF)),

        mutableListOf(SymbolName(NonTerminal.COMANDO)),

        mutableListOf(SymbolName(NonTerminal.BLOCO)),

        mutableListOf(SymbolName(TokenName.ELSEIF), SymbolName(TokenName.LBRACKET), SymbolName(NonTerminal.CONDICAO), SymbolName(TokenName.RBRACKET), SymbolName(TokenName.THEN), SymbolName(NonTerminal.CMD_BLOCO), SymbolName(NonTerminal.ELSE_IF)),

        mutableListOf(SymbolName(TokenName.ELSE), SymbolName(NonTerminal.CMD_BLOCO)),

        mutableListOf(SymbolName(TokenName.EPSILON)),

        mutableListOf(SymbolName(TokenName.WHILE), SymbolName(TokenName.LBRACKET), SymbolName(NonTerminal.CONDICAO), SymbolName(TokenName.RBRACKET), SymbolName(TokenName.DO), SymbolName(NonTerminal.CMD_BLOCO)),

        mutableListOf(SymbolName(TokenName.DO), SymbolName(NonTerminal.CMD_BLOCO), SymbolName(TokenName.WHILE), SymbolName(TokenName.LBRACKET), SymbolName(NonTerminal.CONDICAO), SymbolName(TokenName.RBRACKET), SymbolName(TokenName.SEP)),

        mutableListOf(SymbolName(TokenName.ID), SymbolName(TokenName.ASSIGN), SymbolName(NonTerminal.EXPRESSAO), SymbolName(TokenName.SEP)),

        mutableListOf(SymbolName(NonTerminal.EXPRESSAO), SymbolName(TokenName.RELOP), SymbolName(NonTerminal.EXPRESSAO)),

        mutableListOf(SymbolName(NonTerminal.TERMO), SymbolName(NonTerminal.EXPRESSAO2)),

        mutableListOf(SymbolName(TokenName.ADD_SUB), SymbolName(NonTerminal.TERMO), SymbolName(NonTerminal.EXPRESSAO2)),

        mutableListOf(SymbolName(TokenName.EPSILON)),

        mutableListOf(SymbolName(NonTerminal.FATOR), SymbolName(NonTerminal.TERMO2)),

        mutableListOf(SymbolName(TokenName.MUL_DIV), SymbolName(NonTerminal.FATOR), SymbolName(NonTerminal.TERMO2)),

        mutableListOf(SymbolName(TokenName.EPSILON)),

        mutableListOf(SymbolName(NonTerminal.POTENCIA), SymbolName(NonTerminal.FATOR2)),

        mutableListOf(SymbolName(TokenName.POWER), SymbolName(NonTerminal.POTENCIA), SymbolName(NonTerminal.FATOR2)),

        mutableListOf(SymbolName(TokenName.EPSILON)),

        mutableListOf(SymbolName(TokenName.ID)),

        mutableListOf(SymbolName(TokenName.CONST_NUM)),

        mutableListOf(SymbolName(TokenName.CONST_CHAR)),

        mutableListOf(SymbolName(TokenName.LPAR), SymbolName(NonTerminal.EXPRESSAO), SymbolName(TokenName.RPAR))
    )

    val table: Map<Pair<NonTerminal, TokenName>, Int> = mapOf(
        Pair(NonTerminal.PROGRAMA, TokenName.PROGRAM) to 0,
        Pair(NonTerminal.BLOCO, TokenName.BLOCKOPEN) to 1,
        Pair(NonTerminal.DECLARACOES, TokenName.TYPE) to 2,
        Pair(NonTerminal.DECLARACOES, TokenName.BLOCKCLOSE) to 3,
        Pair(NonTerminal.DECLARACAO, TokenName.TYPE) to 4,

        Pair(NonTerminal.DECLARACOES, TokenName.IF) to 3,
        Pair(NonTerminal.DECLARACOES, TokenName.WHILE) to 3,
        Pair(NonTerminal.DECLARACOES, TokenName.DO) to 3,
        Pair(NonTerminal.DECLARACOES, TokenName.ID) to 3,
        Pair(NonTerminal.DECLARACOES, TokenName.BLOCKCLOSE) to 3,

        Pair(NonTerminal.LISTA_IDS, TokenName.ID) to 5,
        Pair(NonTerminal.LISTA_IDS2, TokenName.COMMA) to 6,
        Pair(NonTerminal.LISTA_IDS2, TokenName.SEP) to 7,

        Pair(NonTerminal.COMANDOS, TokenName.ID) to 8,
        Pair(NonTerminal.COMANDOS, TokenName.IF) to 8,
        Pair(NonTerminal.COMANDOS, TokenName.WHILE) to 8,
        Pair(NonTerminal.COMANDOS, TokenName.DO) to 8,
        Pair(NonTerminal.COMANDOS, TokenName.BLOCKCLOSE) to 9,

        Pair(NonTerminal.COMANDO, TokenName.IF) to 10,
        Pair(NonTerminal.COMANDO, TokenName.WHILE) to 11,
        Pair(NonTerminal.COMANDO, TokenName.DO) to 12,
        Pair(NonTerminal.COMANDO, TokenName.ID) to 13,

        Pair(NonTerminal.SELECAO, TokenName.IF) to 14,

        Pair(NonTerminal.CMD_BLOCO, TokenName.ID) to 15,
        Pair(NonTerminal.CMD_BLOCO, TokenName.IF) to 15,
        Pair(NonTerminal.CMD_BLOCO, TokenName.WHILE) to 15,
        Pair(NonTerminal.CMD_BLOCO, TokenName.DO) to 15,
        Pair(NonTerminal.CMD_BLOCO, TokenName.BLOCKOPEN) to 16,

        Pair(NonTerminal.ELSE_IF, TokenName.ELSEIF) to 17,
        Pair(NonTerminal.ELSE_IF, TokenName.ELSE) to 18,
        Pair(NonTerminal.ELSE_IF, TokenName.ID) to 19,
        Pair(NonTerminal.ELSE_IF, TokenName.IF) to 19,
        Pair(NonTerminal.ELSE_IF, TokenName.WHILE) to 19,
        Pair(NonTerminal.ELSE_IF, TokenName.DO) to 19,
        Pair(NonTerminal.ELSE_IF, TokenName.BLOCKCLOSE) to 19,

        Pair(NonTerminal.REPETICAO_WD, TokenName.WHILE) to 20,
        Pair(NonTerminal.REPETICAO_DW, TokenName.DO) to 21,
        Pair(NonTerminal.ATRIBUICAO, TokenName.ID) to 22,

        Pair(NonTerminal.CONDICAO, TokenName.ID) to 23,
        Pair(NonTerminal.CONDICAO, TokenName.CONST_NUM) to 23,
        Pair(NonTerminal.CONDICAO, TokenName.CONST_CHAR) to 23,
        Pair(NonTerminal.CONDICAO, TokenName.LPAR) to 23,

        Pair(NonTerminal.EXPRESSAO, TokenName.ID) to 24,
        Pair(NonTerminal.EXPRESSAO, TokenName.CONST_NUM) to 24,
        Pair(NonTerminal.EXPRESSAO, TokenName.CONST_CHAR) to 24,
        Pair(NonTerminal.EXPRESSAO, TokenName.LPAR) to 24,

        Pair(NonTerminal.TERMO, TokenName.ID) to 27,
        Pair(NonTerminal.TERMO, TokenName.CONST_NUM) to 27,
        Pair(NonTerminal.TERMO, TokenName.CONST_CHAR) to 27,
        Pair(NonTerminal.TERMO, TokenName.LPAR) to 27,

        Pair(NonTerminal.FATOR, TokenName.ID) to 30,
        Pair(NonTerminal.FATOR, TokenName.CONST_NUM) to 30,
        Pair(NonTerminal.FATOR, TokenName.CONST_CHAR) to 30,
        Pair(NonTerminal.FATOR, TokenName.LPAR) to 30,

        Pair(NonTerminal.POTENCIA, TokenName.ID) to 33,
        Pair(NonTerminal.POTENCIA, TokenName.CONST_NUM) to 34,
        Pair(NonTerminal.POTENCIA, TokenName.CONST_CHAR) to 35,
        Pair(NonTerminal.POTENCIA, TokenName.LPAR) to 36,

        Pair(NonTerminal.EXPRESSAO2, TokenName.ADD_SUB) to 25,
        Pair(NonTerminal.EXPRESSAO2, TokenName.RELOP) to 26,
        Pair(NonTerminal.EXPRESSAO2, TokenName.RBRACKET) to 26,
        Pair(NonTerminal.EXPRESSAO2, TokenName.SEP) to 26,

        Pair(NonTerminal.TERMO2, TokenName.MUL_DIV) to 28,
        Pair(NonTerminal.TERMO2, TokenName.ADD_SUB) to 29,
        Pair(NonTerminal.TERMO2, TokenName.RELOP) to 29,
        Pair(NonTerminal.TERMO2, TokenName.RBRACKET) to 29,
        Pair(NonTerminal.TERMO2, TokenName.SEP) to 29,

        Pair(NonTerminal.FATOR2, TokenName.POWER) to 31,
        Pair(NonTerminal.FATOR2, TokenName.MUL_DIV) to 32,
        Pair(NonTerminal.FATOR2, TokenName.ADD_SUB) to 32,
        Pair(NonTerminal.FATOR2, TokenName.RELOP) to 32,
        Pair(NonTerminal.FATOR2, TokenName.RBRACKET) to 32,
        Pair(NonTerminal.FATOR2, TokenName.SEP) to 32
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
                    throw Exception(
                        "Erro de sintaxe (linha:${nextToken.loc.first}, coluna:${nextToken.loc.second}):\n" +
                        "Token encontrado: ${nextToken.name}\n" +
                        "Token esperado: ${symbol.terminal}"
                    )
                }
            }
            else {
                val production = table.getOrDefault(Pair(symbol.nonTerminal!!, nextToken.name), -1)
                if (production == -1) {
                    val expectedTokens = table.toList()
                        .filter { it.first.first == symbol.nonTerminal!! }
                        .map { it.first.second }

                    throw Exception(
                        "Erro de sintaxe (linha:${nextToken.loc.first}, coluna:${nextToken.loc.second}):\n" +
                        "Token encontrado: ${nextToken.name}\n" +
                        "Tokens esperados: ${expectedTokens.joinToString()}"
                    )
                } else {
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
            throw Exception(
                "Erro de sintaxe (linha:${nextToken.loc.first}, coluna:${nextToken.loc.second}):\n" +
                "Token encontrado: ${nextToken.name}\n" +
                "Esperado: Fim do Arquivo"
            )
        }
        return root
    }
}