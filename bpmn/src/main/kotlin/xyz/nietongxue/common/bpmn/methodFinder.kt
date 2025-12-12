package xyz.nietongxue.common.bpmn

import java.lang.reflect.Method

data class MethodSearchCriteria(
    val methodName: String, val parameterTypes: List<String>
)

object MethodFinder {
    fun findMethod(clazz: Class<*>, criteria: MethodSearchCriteria): Method? {
        return try {
            clazz.methods.first { method ->
                method.name == criteria.methodName
                        && method.parameterCount == criteria.parameterTypes.size
                        && method.parameterTypes.withIndex()
                    .all {
                        println( it)
                        val parameterType = criteria.parameterTypes[it.index]
                        parameterType == "java.lang.Object" || it.value.name == parameterType //TODO 改成继承关系的判断
                    }
            }
        } catch (e: NoSuchMethodException) {
            null
        }
    }

}