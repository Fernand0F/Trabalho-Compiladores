package token.literal

import token.Token

abstract class LiteralToken(val type: DataType): Token("LITERAL")