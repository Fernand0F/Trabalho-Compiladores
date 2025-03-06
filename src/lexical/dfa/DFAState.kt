package lexical.dfa

data class DFAState<T>(
    var state: T,
    var isAcceptState: Boolean,
    var isErrorState: Boolean,
    var isLookAheadState: Boolean
)