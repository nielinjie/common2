package xyz.nietongxue.common.bpmn

import com.alibaba.compileflow.engine.core.infrastructure.utils.ClassUtils
import java.lang.reflect.Method


class MapActionEx {
    fun run(
        collection: ArrayList<String>,
        targetClazz: String,
        targetMethod: String,
        byPass: Object?
    ): HashMap<String, String> {
        val obj = ClassUtils.newInstance<Any>(targetClazz)
        val clazz = Class.forName(targetClazz)
        val paraTypes = listOf("java.lang.String", "java.lang.Object", "java.lang.Integer")
        val paraTypesNoIndex = listOf("java.lang.String", "java.lang.Object")
        var method = MethodFinder.findMethod(clazz, MethodSearchCriteria(targetMethod, paraTypesNoIndex))!!

        println("method by paraTypes - $method")


        val result = HashMap<String, String>()
        collection.withIndex().forEach {
//            val re = methodAndParaList.let { (method, paraTypeList) ->
//                if (paraTypeList.size == 2)
//                    method.invoke(obj, it.value, byPass) as String
//                else
//                    method.invoke(obj, it.value, byPass, it.index) as String
//            }
            val re = method.invoke(obj, it.value, byPass) as String
            result[it.index.toString()] = re
        }
        return result
    }
}

class Mapping(
    val action: Action,
    val collection: String,
    val resultContextName: String,
    val byPassVar: String? = null,
) : ActionDefine {
    override fun generate(): Action {
        return when (action) {
            is ObjectMethodAction -> {
                MapAction(
                    action.clazz, action.method,
                    collection, resultContextName, byPassVar
                ).generate()
            }

            else -> error("not support yet")
        }
    }

}

class MapAction(
    val targetClazz: String,
    val targetMethod: String,
    val collection: String,
    val resultContextName: String,
    val byPassVar: String? = null,
) : ActionDefine {
    override fun generate(): Action {
        return ObjectMethodAction(
            MapActionEx::class.java.canonicalName,
            "run",
            inputs = listOfNotNull(
                Action.Input("collection", "java.util.ArrayList<String>", collection),
                Action.Input("targetClazz", "java.lang.String", defaultValue = targetClazz),
                Action.Input("targetMethod", "java.lang.String", defaultValue = targetMethod),
                Action.Input("byPassVar", "java.lang.Object", contextVarName = byPassVar)
            ),

            outputs = listOf(
                Action.Output("resultMap", "java.util.HashMap<String,String>", resultContextName)
            )
        )
    }

}