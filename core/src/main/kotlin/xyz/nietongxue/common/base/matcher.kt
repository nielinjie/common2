package xyz.nietongxue.common.base

interface Matcher<I, C> {
    fun match(input: I, context: C): Boolean
}

data class AndMatcher<I, C>(val matchers: List<Matcher<I, C>>) : Matcher<I, C> {
    override fun match(input: I, context: C): Boolean {
        if (matchers.isEmpty()) return true
        return matchers.all { it.match(input, context) }
    }
}
data class OrMatcher<I, C>(val matchers: List<Matcher<I, C>>) : Matcher<I, C> {
    init {
        require(matchers.isNotEmpty()){
            "OrMatcher must have at least one matcher"
        }
    }
    override fun match(input: I, context: C): Boolean {
        return matchers.any { it.match(input, context) }
    }
}
data class NotMatcher<I, C>(val matcher: Matcher<I, C>) : Matcher<I, C> {
    override fun match(input: I, context: C): Boolean {
        return !matcher.match(input, context)
    }
}