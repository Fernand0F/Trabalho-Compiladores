fun main() {
    val lex = Setup.getLex()
        .setInput("src/input.txt")

    do {
        val token = lex.nextToken()
        println(token)
    } while(token != null)
}