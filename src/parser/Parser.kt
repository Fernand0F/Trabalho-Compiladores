package parser

import lexical.Lexical
import token.Token
import token.TokenName
import java.util.Stack

class Parser(val lexer: Lexical) {
    val productions: MutableList<MutableList<SymbolName>> = mutableListOf(
        // PROGRAMA → program id lpar rpar BLOCO
        mutableListOf(SymbolName(TokenName.PROGRAM), SymbolName(TokenName.ID), SymbolName(TokenName.LPAR), SymbolName(TokenName.RPAR), SymbolName(NonTerminal.BLOCO)),

        // BLOCO → blockopen DECLARACOES COMANDOS blockclose
        mutableListOf(SymbolName(TokenName.BLOCKOPEN), SymbolName(NonTerminal.DECLARACOES), SymbolName(NonTerminal.COMANDOS), SymbolName(TokenName.BLOCKCLOSE)),

        // DECLARACOES → DECLARACAO DECLARACOES
        mutableListOf(SymbolName(NonTerminal.DECLARACAO), SymbolName(NonTerminal.DECLARACOES)),

        // DECLARACOES → ε
        mutableListOf(SymbolName(TokenName.EPSILON)),

        // DECLARACAO → type colon LISTA_IDS sep
        mutableListOf(SymbolName(TokenName.TYPE), SymbolName(TokenName.COLON), SymbolName(NonTerminal.LISTA_IDS), SymbolName(TokenName.SEP)),

        // LISTA_IDS → id LISTA_IDS'
        mutableListOf(SymbolName(TokenName.ID), SymbolName(NonTerminal.LISTA_IDS2)),

        // LISTA_IDS' → comma LISTA_IDS
        mutableListOf(SymbolName(TokenName.COMMA), SymbolName(NonTerminal.LISTA_IDS)),

        // LISTA_IDS' → ε
        mutableListOf(SymbolName(TokenName.EPSILON)),

        // COMANDOS → COMANDO COMANDOS
        mutableListOf(SymbolName(NonTerminal.COMANDO), SymbolName(NonTerminal.COMANDOS)),

        // COMANDOS → ε
        mutableListOf(SymbolName(TokenName.EPSILON)),

        // COMANDO → SELECAO
        mutableListOf(SymbolName(NonTerminal.SELECAO)),

        // COMANDO → REPETICAO_WD
        mutableListOf(SymbolName(NonTerminal.REPETICAO_WD)),

        // COMANDO → REPETICAO_DW
        mutableListOf(SymbolName(NonTerminal.REPETICAO_DW)),

        // COMANDO → ATRIBUICAO
        mutableListOf(SymbolName(NonTerminal.ATRIBUICAO)),

        // SELECAO → if lbracket CONDICAO rbracket then CMD_BLOCO ELSE_IF
        mutableListOf(SymbolName(TokenName.IF), SymbolName(TokenName.LBRACKET), SymbolName(NonTerminal.CONDICAO), SymbolName(TokenName.RBRACKET), SymbolName(TokenName.THEN), SymbolName(NonTerminal.CMD_BLOCO), SymbolName(NonTerminal.ELSE_IF)),

        // CMD_BLOCO → COMANDO
        mutableListOf(SymbolName(NonTerminal.COMANDO)),

        // CMD_BLOCO → BLOCO
        mutableListOf(SymbolName(NonTerminal.BLOCO)),

        // ELSE_IF → elseif lbracket CONDICAO rbracket then CMD_BLOCO ELSE_IF
        mutableListOf(SymbolName(TokenName.ELSEIF), SymbolName(TokenName.LBRACKET), SymbolName(NonTerminal.CONDICAO), SymbolName(TokenName.RBRACKET), SymbolName(TokenName.THEN), SymbolName(NonTerminal.CMD_BLOCO), SymbolName(NonTerminal.ELSE_IF)),

        // ELSE_IF → else CMD_BLOCO
        mutableListOf(SymbolName(TokenName.ELSE), SymbolName(NonTerminal.CMD_BLOCO)),

        // ELSE_IF → ε
        mutableListOf(SymbolName(TokenName.EPSILON)),

        // REPETICAO_WD → while lbracket CONDICAO rbracket do CMD_BLOCO
        mutableListOf(SymbolName(TokenName.WHILE), SymbolName(TokenName.LBRACKET), SymbolName(NonTerminal.CONDICAO), SymbolName(TokenName.RBRACKET), SymbolName(TokenName.DO), SymbolName(NonTerminal.CMD_BLOCO)),

        // REPETICAO_DW → do CMD_BLOCO while lbracket CONDICAO rbracket sep
        mutableListOf(SymbolName(TokenName.DO), SymbolName(NonTerminal.CMD_BLOCO), SymbolName(TokenName.WHILE), SymbolName(TokenName.LBRACKET), SymbolName(NonTerminal.CONDICAO), SymbolName(TokenName.RBRACKET), SymbolName(TokenName.SEP)),

        // ATRIBUICAO → id assign EXPRESSAO sep
        mutableListOf(SymbolName(TokenName.ID), SymbolName(TokenName.ASSIGN), SymbolName(NonTerminal.EXPRESSAO), SymbolName(TokenName.SEP)),

        // CONDICAO → EXPRESSAO relop EXPRESSAO
        mutableListOf(SymbolName(NonTerminal.EXPRESSAO), SymbolName(TokenName.RELOP), SymbolName(NonTerminal.EXPRESSAO)),

        // EXPRESSAO → TERMO EXPRESSAO'
        mutableListOf(SymbolName(NonTerminal.TERMO), SymbolName(NonTerminal.EXPRESSAO2)),

        // EXPRESSAO' → add_sub TERMO EXPRESSAO'
        mutableListOf(SymbolName(TokenName.ADD_SUB), SymbolName(NonTerminal.TERMO), SymbolName(NonTerminal.EXPRESSAO2)),

        // EXPRESSAO' → ε
        mutableListOf(SymbolName(TokenName.EPSILON)),

        // TERMO → FATOR TERMO'
        mutableListOf(SymbolName(NonTerminal.FATOR), SymbolName(NonTerminal.TERMO2)),

        // TERMO' → mul_div FATOR TERMO'
        mutableListOf(SymbolName(TokenName.MUL_DIV), SymbolName(NonTerminal.FATOR), SymbolName(NonTerminal.TERMO2)),

        // TERMO' → ε
        mutableListOf(SymbolName(TokenName.EPSILON)),

        // FATOR → POTENCIA FATOR'
        mutableListOf(SymbolName(NonTerminal.POTENCIA), SymbolName(NonTerminal.FATOR2)),

        // FATOR' → power POTENCIA FATOR'
        mutableListOf(SymbolName(TokenName.POWER), SymbolName(NonTerminal.POTENCIA), SymbolName(NonTerminal.FATOR2)),

        // FATOR' → ε
        mutableListOf(SymbolName(TokenName.EPSILON)),

        // POTENCIA → id
        mutableListOf(SymbolName(TokenName.ID)),

        // POTENCIA → const_num
        mutableListOf(SymbolName(TokenName.CONST_NUM)),

        // POTENCIA → const_char
        mutableListOf(SymbolName(TokenName.CONST_CHAR)),

        // POTENCIA → lpar EXPRESSAO rpar
        mutableListOf(SymbolName(TokenName.LPAR), SymbolName(NonTerminal.EXPRESSAO), SymbolName(TokenName.RPAR))
    )

    val table: Map<Pair<NonTerminal, TokenName>, Int> = mapOf(
        // 3.3.1 - Grupo Inicial, Bloco e Declarações
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

        // 3.3.2 - Lista de Identificadores
        Pair(NonTerminal.LISTA_IDS, TokenName.ID) to 5,
        Pair(NonTerminal.LISTA_IDS2, TokenName.COMMA) to 6,
        Pair(NonTerminal.LISTA_IDS2, TokenName.SEP) to 7,

        // 3.3.3 - Comandos e Seleção
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

        // 3.3.4 - Estrutura Condicional (ELSE_IF)
        Pair(NonTerminal.ELSE_IF, TokenName.ELSEIF) to 17,
        Pair(NonTerminal.ELSE_IF, TokenName.ELSE) to 18,
        Pair(NonTerminal.ELSE_IF, TokenName.ID) to 19,
        Pair(NonTerminal.ELSE_IF, TokenName.IF) to 19,
        Pair(NonTerminal.ELSE_IF, TokenName.WHILE) to 19,
        Pair(NonTerminal.ELSE_IF, TokenName.DO) to 19,
        Pair(NonTerminal.ELSE_IF, TokenName.BLOCKCLOSE) to 19,

        // 3.3.5 - Repetições e Atribuição
        Pair(NonTerminal.REPETICAO_WD, TokenName.WHILE) to 20,
        Pair(NonTerminal.REPETICAO_DW, TokenName.DO) to 21,
        Pair(NonTerminal.ATRIBUICAO, TokenName.ID) to 22,

        // 3.3.6 - Condições e Expressões
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

        // 3.3.7 - Sufixos de Expressões
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
            println(stack)
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