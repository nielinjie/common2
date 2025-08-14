package xyz.nietongxue.common.base


sealed interface Quantifier {
    fun match(number: Int): Boolean
    data class Many(val number: Int?) : Quantifier {
        override fun match(number: Int): Boolean {
            return this.number?.let { number == it } ?: (number > 1)
        }
    }

    data object One : Quantifier {
        override fun match(number: Int): Boolean {
            return number == 1
        }
    }

    data object ZeroOrOne : Quantifier {
        override fun match(number: Int): Boolean {
            return number in 0..1
        }
    }

    data object OneOrMore : Quantifier {
        override fun match(number: Int): Boolean {
            return number >= 1
        }
    }

    data object ZeroOrMany : Quantifier {
        override fun match(number: Int): Boolean {
            return true
        }
    }
}