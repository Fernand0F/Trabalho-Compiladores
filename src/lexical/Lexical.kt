package lexical

import Globals
import symboltable.*
import symboltable.entry.literal.CharLiteralEntry
import symboltable.entry.literal.FloatLiteralEntry
import symboltable.entry.literal.IntLiteralEntry
import symboltable.entry.TableEntry
import token.Token
import token.TokenName
import token.attr.*

class Lexical(private val symbolTable: SymbolTable) {
    private var fileReader: FileReader? = null
    private var rowCount = 1
    private var columnCount = 1

    fun setInput(filepath: String) = apply {
        fileReader = FileReader(filepath)
    }

    fun nextToken(): Token? {
        checkNotNull(fileReader) { "File input must be defined" }

        // Look for file end
        if (fileReader!!.getChar() == null)
            return null

        var lexeme = ""
        var state = 0
        var lexemeStart = Pair(rowCount, columnCount)

        while(true) {
            val c = fileReader!!.getChar() ?: Globals.EOF_CHAR
            var isLookAhead = false
            var restart = false
            var token: Token? = null

            when (state) {
                0 -> {
                    when {
                        c in setOf(' ', '\t', '\n', '\r') -> state = 1
                        c.isDigit() -> state = 3
                        c == '\'' -> state = 11
                        c == '{' -> state = 15
                        c == '%' -> state = 20
                        c == '[' -> state = 22
                        c == ']' -> state = 23
                        c == ':' -> state = 24
                        c == ';' -> state = 27
                        c == '(' -> state = 28
                        c == ')' -> state = 29
                        c == ',' -> state = 30
                        c == '+' -> state = 31
                        c == '-' -> state = 32
                        c == '/' -> state = 33
                        c == '*' -> state = 34
                        c == '=' -> state = 37
                        c == '<' -> state = 38
                        c == '>' -> state = 42
                        c == 'f' -> state = 45
                        c == 'c' -> state = 51
                        c == 'i' -> state = 56
                        c == 't' -> state = 62
                        c == 'e' -> state = 67
                        c == 'd' -> state = 75
                        c == 'w' -> state = 78
                        c == 'p' -> state = 84
                        c in "abghjklmnoqrsuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_" -> state = 93
                        else -> state = 200
                    }
                }
                1 -> {
                    when {
                        c in setOf(' ', '\t', '\n', '\r') -> state = 1
                        else -> {state = 2; isLookAhead = true}
                    }
                }
                2 -> {
                    isLookAhead = true
                    restart = true
                }
                3 -> {
                    when {
                        c.isDigit() -> state = 3
                        c == '.' -> state = 4
                        c == 'E' -> state = 6
                        else -> {state = 9; isLookAhead = true}
                    }
                }
                4 -> {
                    when {
                        c.isDigit() -> state = 5
                        else -> state = 200
                    }
                }
                5 -> {
                    when {
                        c.isDigit() -> state = 5
                        c == 'E' -> state = 6
                        else -> {state = 10; isLookAhead = true}
                    }
                }
                6 -> {
                    when {
                        c == '+' || c == '-' -> state = 7
                        c.isDigit() -> state = 8
                        else -> state = 200
                    }
                }
                7 -> {
                    when {
                        c.isDigit() -> state = 8
                        else -> state = 200
                    }
                }
                8 -> {
                    when {
                        c.isDigit() -> state = 8
                        else -> {state = 10; isLookAhead = true}
                    }
                }
                9 -> {
                    isLookAhead = true
                    token = Token(
                        TokenName.CONST_NUM,
                        lexemeStart,
                        IdAttr(symbolTable.get(IntLiteralEntry(lexeme, lexeme.toInt())))
                    )
                }
                10 -> {
                    isLookAhead = true
                    token = Token(
                        TokenName.CONST_NUM,
                        lexemeStart,
                        IdAttr(symbolTable.get(FloatLiteralEntry(lexeme, lexeme.toFloat())))
                    )
                }
                11 -> {
                    when (c) {
                        '\\' -> state = 13
                        '\'' -> state = 200
                        else -> state = 12
                    }
                }
                12 -> {
                    when (c) {
                        '\'' -> state = 14
                        else -> state = 200
                    }
                }
                13 -> {
                    when (c) {
                        '\'', '\\' -> state = 12
                        else -> state = 200
                    }
                }
                14 -> {
                    val cLex = if (lexeme[1] == '\\') lexeme[2] else lexeme[1]
                    token = Token(
                        TokenName.CONST_CHAR,
                        lexemeStart,
                        IdAttr(symbolTable.get(CharLiteralEntry(cLex.toString(), cLex)))
                    )
                }
                15 -> {
                    when (c) {
                        '#' -> state = 16
                        '%' -> state = 19
                        else -> state = 200
                    }
                }
                16 -> {
                    when (c) {
                        '#' -> state = 17
                        else -> state = 16
                    }
                }
                17 -> {
                    when (c) {
                        '#' -> state = 17
                        '}' -> state = 18
                        else -> state = 16
                    }
                }
                18 -> {
                    restart = true
                }
                19 -> {
                    token = Token(TokenName.BLOCKOPEN, lexemeStart, null)
                }
                20 -> {
                    when (c) {
                        '}' -> state = 21
                        else -> state = 200
                    }
                }
                21 -> {
                    token = Token(TokenName.BLOCKCLOSE, lexemeStart, null)
                }
                22 -> {
                    token = Token(TokenName.LBRACKET, lexemeStart, null)
                }
                23 -> {
                    token = Token(TokenName.RBRACKET, lexemeStart, null)
                }
                24 -> {
                    when (c) {
                        '=' -> state = 25
                        else -> {state = 26; isLookAhead = true}
                    }
                }
                25 -> {
                    token = Token(TokenName.ASSIGN, lexemeStart, null)
                }
                26 -> {
                    isLookAhead = true
                    token = Token(TokenName.COLON, lexemeStart, null)
                }
                27 -> {
                    token = Token(TokenName.SEP, lexemeStart, null)
                }
                28 -> {
                    token = Token(TokenName.LPAR, lexemeStart, null)
                }
                29 -> {
                    token = Token(TokenName.RPAR, lexemeStart, null)
                }
                30 -> {
                    token = Token(TokenName.COMMA, lexemeStart, null)
                }
                31 -> {
                    token = Token(TokenName.ADD_SUB, lexemeStart, AritopAttr(Aritop.SUM))
                }
                32 -> {
                    token = Token(TokenName.ADD_SUB, lexemeStart, AritopAttr(Aritop.SUB))
                }
                33 -> {
                    token = Token(TokenName.MUL_DIV, lexemeStart, AritopAttr(Aritop.DIV))
                }
                34 -> {
                    when (c) {
                        '*' -> state = 35
                        else -> {state = 36; isLookAhead = true}
                    }
                }
                35 -> {
                    token = Token(TokenName.POWER, lexemeStart, null)
                }
                36 -> {
                    token = Token(TokenName.MUL_DIV, lexemeStart, AritopAttr(Aritop.MUL))
                }
                37 -> {
                    token = Token(TokenName.RELOP, lexemeStart, RelopAttr(Relop.EQ))
                }
                38 -> {
                    when (c) {
                        '>' -> state = 39
                        '=' -> state = 40
                        else -> {state = 41; isLookAhead = true}
                    }
                }
                39 -> {
                    token = Token(TokenName.RELOP, lexemeStart, RelopAttr(Relop.NE))
                }
                40 -> {
                    token = Token(TokenName.RELOP, lexemeStart, RelopAttr(Relop.LE))
                }
                41 -> {
                    isLookAhead = true
                    token = Token(TokenName.RELOP, lexemeStart, RelopAttr(Relop.LT))
                }
                42 -> {
                    when (c) {
                        '=' -> state = 43
                        else -> {state = 44; isLookAhead = true}
                    }
                }
                43 -> {
                    token = Token(TokenName.RELOP, lexemeStart, RelopAttr(Relop.GE))
                }
                44 -> {
                    isLookAhead = true
                    token = Token(TokenName.RELOP, lexemeStart, RelopAttr(Relop.GT))
                }
                45 -> {
                    when {
                        c == 'l' -> state = 46
                        alphaNum(c) -> state = 93
                        else -> {state = 94; isLookAhead = true}
                    }
                }
                46 -> {
                    when {
                        c == 'o' -> state = 47
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                47 -> {
                    when {
                        c == 'a' -> state = 48
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                48 -> {
                    when {
                        c == 't' -> state = 49
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                49 -> {
                    when {
                        alphaNum(c) -> state = 93
                        else -> {state = 50; isLookAhead = true}
                    }
                }
                50 -> {
                    isLookAhead = true
                    token = Token(TokenName.TYPE, lexemeStart, TypeAttr(DataType.FLOAT))
                }
                51 -> {
                    when {
                        c == 'h' -> state = 52
                        alphaNum(c) -> state = 93
                        else -> {state = 94; isLookAhead = true}
                    }
                }
                52 -> {
                    when {
                        c == 'a' -> state = 53
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                53 -> {
                    when {
                        c == 'r' -> state = 54
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                54 -> {
                    when {
                        alphaNum(c) -> state = 93
                        else -> {state = 55; isLookAhead = true}
                    }
                }
                55 -> {
                    isLookAhead = true
                    token = Token(TokenName.TYPE, lexemeStart, TypeAttr(DataType.CHAR))
                }
                56 -> {
                    when {
                        c == 'n' -> state = 57
                        c == 'f' -> state = 60
                        alphaNum(c) -> state = 93
                        else -> {state = 94; isLookAhead = true}
                    }
                }
                57 -> {
                    when {
                        c == 't' -> state = 58
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                58 -> {
                    when {
                        alphaNum(c) -> state = 93
                        else -> {state = 59; isLookAhead = true}
                    }
                }
                59 -> {
                    isLookAhead = true
                    token = Token(TokenName.TYPE, lexemeStart, TypeAttr(DataType.INT))
                }
                60 -> {
                    when {
                        alphaNum(c) -> state = 93
                        else -> {state = 61; isLookAhead = true}
                    }
                }
                61 -> {
                    isLookAhead = true
                    token = Token(TokenName.IF, lexemeStart, null)
                }
                62 -> {
                    when {
                        c == 'h' -> state = 63
                        alphaNum(c) -> state = 93
                        else -> {state = 94; isLookAhead = true}
                    }
                }
                63 -> {
                    when {
                        c == 'e' -> state = 64
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                64 -> {
                    when {
                        c == 'n' -> state = 65
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                65 -> {
                    when {
                        alphaNum(c) -> state = 93
                        else -> {state = 66; isLookAhead = true}
                    }
                }
                66 -> {
                    isLookAhead = true
                    token = Token(TokenName.THEN, lexemeStart, null)
                }
                67 -> {
                    when {
                        c == 'l' -> state = 68
                        alphaNum(c) -> state = 93
                        else -> {state = 94; isLookAhead = true}
                    }
                }
                68 -> {
                    when {
                        c == 's' -> state = 69
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                69 -> {
                    when {
                        c == 'e' -> state = 70
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                70 -> {
                    when {
                        c == 'i' -> state = 72
                        alphaNum(c) -> state = 93
                        else -> {state = 71; isLookAhead = true}
                    }
                }
                71 -> {
                    isLookAhead = true
                    token = Token(TokenName.ELSE, lexemeStart, null)
                }
                72 -> {
                    when {
                        c == 'f' -> state = 73
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                73 -> {
                    when {
                        alphaNum(c) -> state = 93
                        else -> {state = 74; isLookAhead = true}
                    }
                }
                74 -> {
                    isLookAhead = true
                    token = Token(TokenName.ELSEIF, lexemeStart, null)
                }
                75 -> {
                    when {
                        c == 'o' -> state = 76
                        alphaNum(c) -> state = 93
                        else -> {state = 94; isLookAhead = true}
                    }
                }
                76 -> {
                    when {
                        alphaNum(c) -> state = 93
                        else -> {state = 77; isLookAhead = true}
                    }
                }
                77 -> {
                    isLookAhead = true
                    token = Token(TokenName.DO, lexemeStart, null)
                }
                78 -> {
                    when {
                        c == 'h' -> state = 79
                        alphaNum(c) -> state = 93
                        else -> {state = 94; isLookAhead = true}
                    }
                }
                79 -> {
                    when {
                        c == 'i' -> state = 80
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                80 -> {
                    when {
                        c == 'l' -> state = 81
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                81 -> {
                    when {
                        c == 'e' -> state = 82
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                82 -> {
                    when {
                        alphaNum(c) -> state = 93
                        else -> {state = 83; isLookAhead = true}
                    }
                }
                83 -> {
                    isLookAhead = true
                    token = Token(TokenName.WHILE, lexemeStart, null)
                }
                84 -> {
                    when {
                        c == 'r' -> state = 85
                        alphaNum(c) -> state = 93
                        else -> {state = 94; isLookAhead = true}
                    }
                }
                85 -> {
                    when {
                        c == 'o' -> state = 86
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                86 -> {
                    when {
                        c == 'g' -> state = 87
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                87 -> {
                    when {
                        c == 'r' -> state = 88
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                88 -> {
                    when {
                        c == 'a' -> state = 89
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                89 -> {
                    when {
                        c == 'm' -> state = 90
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                90 -> {
                    when {
                        c == 'a' -> state = 91
                        alphaNum(c) -> state = 93
                        else -> state = 94
                    }
                }
                91 -> {
                    when {
                        alphaNum(c) -> state = 92
                        else -> {state = 92; isLookAhead = true}
                    }
                }
                92 -> {
                    isLookAhead = true
                    token = Token(TokenName.PROGRAM, lexemeStart, null)
                }
                93 -> {
                    when {
                        alphaNum(c) -> state = 93
                        else -> {state = 94; isLookAhead = true}
                    }
                }
                94 -> {
                    isLookAhead = true
                    token = Token(
                        TokenName.ID,
                        lexemeStart,
                        IdAttr(symbolTable.get(TableEntry(TokenName.ID, lexeme))))
                }
                200 -> {fail(lexeme, lexemeStart)}
            }

            if (token != null)
                return token

            if (!isLookAhead) {
                lexeme += c
                fileReader!!.next()
                processRowColumn(c)
            }

            if (restart)
                return nextToken()
        }   
    }

    private fun processRowColumn(c: Char) {
        if (c == '\n') {
            rowCount++
            columnCount = 1
        } else {
            columnCount++
        }
    }
    
    private fun fail(lexeme: String, lexemeStart: Pair<Int, Int>) {
        throw Exception("Malformed Token: \"$lexeme\"[row: ${lexemeStart.first}, column: ${lexemeStart.second}]")
    }

    private fun alphaNum(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c.isDigit() || c == '_'
    }
}
