package lexical.dfa

import Globals

class DFABuilder<T> {
    private var initialState: T? = null
    private val transitions = mutableMapOf<Pair<T, Char>, T>()
    private val accept = mutableSetOf<T>()
    private val lookAhead = mutableMapOf<T, T>()
    private val alphabet = mutableSetOf(Globals.EOF_CHAR)

    fun initialState(s: T) = apply { initialState = s }

    fun transition(s0: T, c: Char, s1: T) = apply {
        transitions[s0 to c] = s1
        alphabet.add(c)
    }

    fun transition(s0: T, cs: Collection<Char>, s1: T) = apply {
        for (c in cs)
            transitions[s0 to c] = s1
        alphabet.addAll(cs)
    }

    fun allDigits(s0: T, s1: T) = apply { transition(s0, ('0'..'9').toList(), s1) }

    fun allLetters(s0: T, s1: T) = apply {
        transition(s0, ('a'..'z').toList(), s1)
        transition(s0, ('A'..'Z').toList(), s1)
    }

    fun lookAhead(s0: T, s1: T) = apply { lookAhead[s0] = s1 }

    fun accept(vararg s: T) = apply { accept.addAll(s) }

    fun compile(): DFA<T> {
        checkNotNull(initialState) { "Initial state must be defined" }

        val compiledTransitions = HashMap(transitions)
        val compiledAccept = HashSet(accept)

        // Set look ahead states and transitions
        val lookAheadStates = lookAhead.toList().fold(mutableSetOf<T>()) { carry, (s0, s1) ->
            if (accept.contains(s0))
                throw IllegalStateException("An accept state ($s0) must not contain a lookahead")

            carry.apply {
                add(s1)
                compiledAccept.add(s1)
                alphabet.forEach { c -> compiledTransitions.getOrPut(s0 to c) { s1 } }
            }
        }

        return DFA(initialState!!, compiledTransitions, compiledAccept, lookAheadStates)
    }
}