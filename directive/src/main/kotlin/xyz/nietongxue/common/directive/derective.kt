package xyz.nietongxue.common.directive

import com.fasterxml.jackson.annotation.JsonIgnore
import org.slf4j.LoggerFactory
import xyz.nietongxue.common.base.Stuff


interface UseDirective {
    var directive: Directive
    fun <T : UseDirective> merge(other: UseDirective?): T {
        return (this as T).also { this.directive = this.directive.merge(other?.directive) }
    }

    fun <T : UseDirective> copy( data: Stuff): T {
        return (this as T).also {
            this.directive = Directive(this.directive.type, data)
        }
    }
    @get:JsonIgnore
    val data: Stuff
        get() {
            return directive.data
        }
}


data class Directive(
    val type: String,
    val data: Stuff = emptyMap()
) {

    @JsonIgnore
    val log = LoggerFactory.getLogger(this.javaClass)
    fun merge(other: Directive?): Directive {
        if (other == null) return this
        if (type != other.type) {
            log.info("type not equal, data will be replaced")
            return Directive(other.type, other.data)
        }
        return Directive(type, data.plus(other.data))
    }
}

