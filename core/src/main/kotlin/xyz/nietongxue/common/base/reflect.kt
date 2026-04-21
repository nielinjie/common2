package xyz.nietongxue.common.base

import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


fun String.forClass(): Class<*> {
    return Class.forName(this)
}

fun <T : Any> KClass<T>.tryNew(): T? {
    return runCatching { this.createInstance() }.getOrNull() ?: runCatching { this.objectInstance }.getOrNull()
}


data class MethodSearchCriteria(
    val methodName: String, val parameterTypes: List<Class<*>>
)

fun findMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Method? {
    return findMethod(clazz, MethodSearchCriteria(methodName, parameterTypes.toList()))
}

fun findMethod(clazz: Class<*>, criteria: MethodSearchCriteria): Method? {
    return clazz.methods.firstOrNull() { method ->
        method.name == criteria.methodName
                && method.parameterCount == criteria.parameterTypes.size
                && method.parameterTypes.withIndex()
            .all {
                val parameterType = criteria.parameterTypes[it.index]
                it.value.isAssignableFrom(parameterType) //TODO 改成继承关系的判断
            }
    }
}