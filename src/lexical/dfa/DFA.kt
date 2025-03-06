package lexical.dfa

class DFA<T>(
    private val initialState: T,
    private val transitions: Map<Pair<T, Char>, T>,
    private val accept: Set<T>,
    private val lookAhead: Set<T>
) {
    private val state = DFAState(
        initialState,
        accept.contains(initialState),
        false,
        lookAhead.contains(initialState)
    )

    fun getState() = state.copy()

    fun reset() = apply {
        state.state = initialState
        state.isAcceptState = accept.contains(initialState)
        state.isErrorState = false
        state.isLookAheadState = lookAhead.contains(initialState)
    }

    fun put(c: Char): DFAState<T> {
        if (state.isErrorState)
            throw IllegalStateException("DFA already in error state")

        transitions[state.state to c]?.let {
            state.state = it
            state.isAcceptState = accept.contains(it)
            state.isLookAheadState = lookAhead.contains(it)
        } ?: run {
            state.isErrorState = true
        }

        return getState()
    }
}