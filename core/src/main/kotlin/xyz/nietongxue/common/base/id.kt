package xyz.nietongxue.common.base

import kotlinx.datetime.Clock
import kotlinx.uuid.UUID
import kotlinx.uuid.UUIDExperimentalAPI
import kotlinx.uuid.UUIDv7


typealias Id = String //使用string作为对外类型，uuid只在新建时有意义。


interface HasId {
    val id: Id
}

typealias IdGetter<I> = (I) -> Id?
typealias ParentGetter<I> = (I) -> Id?

val defaultIdNewer: () -> Id = { id() }

interface IdCreateStrategy{
    object V7 :IdCreateStrategy
    object V4:IdCreateStrategy
    class V5(val string:String): IdCreateStrategy
}

fun id(idCreateStrategy: IdCreateStrategy = IdCreateStrategy.V7): Id  {
    return when(idCreateStrategy){
        IdCreateStrategy.V7 -> v7()
        IdCreateStrategy.V4 -> randomId()
        is IdCreateStrategy.V5 -> TODO()
        else -> error("not supported idCreateStrategy $idCreateStrategy")
    }
}
fun randomId(): Id = UUID().toString()
@OptIn(UUIDExperimentalAPI::class)
fun v7(): Id = UUIDv7(Clock.System.now().toEpochMilliseconds()).toString() // apply v7 uuid
