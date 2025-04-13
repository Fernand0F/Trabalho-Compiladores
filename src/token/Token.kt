package token

import Globals
import token.attr.TokenAttr

class Token(val name: TokenName, var loc: Pair<Int, Int>, var attr: TokenAttr?) {
    override fun toString(): String {
        val s = "[${Globals.BLUE}$name${Globals.RESET_COLOR} loc=(${loc.first}, ${loc.second})"
        when (attr) {
            null -> return "$s]"
            else -> return "$s attr=($attr)]"
        }
    }
}