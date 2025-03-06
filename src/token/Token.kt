package token

import Globals
import token.attr.TokenAttr

class Token(val name: String, val loc: Pair<Int, Int>, val attr: TokenAttr?) {
    private val str = buildString {
        append("[${Globals.BLUE}$name${Globals.RESET_COLOR} loc=(${loc.first}, ${loc.second})")
        if (attr != null)
            append(" attr=($attr)")
        append("]")
    }

    override fun toString() = str
}