package xyz.nietongxue.common.runtime.run

import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.Logger
import xyz.nietongxue.common.base.findMethod
import xyz.nietongxue.common.base.forClass
import xyz.nietongxue.common.base.tryNew
import xyz.nietongxue.common.collections.toNoNullValueMap
import xyz.nietongxue.common.http.HttpCaller
import xyz.nietongxue.common.runtime.callable.ARoundRuntimeCallable
import xyz.nietongxue.common.runtime.callable.BeanRuntime
import xyz.nietongxue.common.runtime.callable.ConstantRuntime
import xyz.nietongxue.common.runtime.callable.RuntimeCallable
import xyz.nietongxue.common.runtime.callable.ScriptRuntime
import xyz.nietongxue.common.runtime.callable.URLRuntime
import xyz.nietongxue.common.scripts.kotlinRun

fun RuntimeCallable.runIt(args: Map<String, Any>, logger: Logger?): Any? {
    return when (this) {
        is BeanRuntime -> {
            val (className, methodName, setupArgs, setupMethod) = this

            className.forClass().let {
                val method = findMethod(it, methodName, Map::class.java)
                (method ?: error("method not find - $className, $methodName")).invoke(
                    it.kotlin.tryNew().also { bean ->
                        val setupM = findMethod(it, setupMethod, ObjectNode::class.java)
                        if (setupM != null) {
                            setupM.invoke(bean, setupArgs)
                        } else {
                            logger?.warn("method not find - $className, $setupMethod")
                        }
                    },
                    args
                )
            }
        }

        is ScriptRuntime -> {
            val (script, language) = this
            val context = args.toNoNullValueMap()
            when (language) {
                "kotlin" -> {
                    kotlinRun(
                        script,
                        context.plus("logger" to logger).toNoNullValueMap(),
                        CustomClassLoader.loader
                    ).first
                }

                else -> {
                    error("language not support YET- $language")
                }

            }
        }

        is URLRuntime -> {
            HttpCaller.call(this.url, inputs = args, callOption = option, logger = logger)
        }

        is ConstantRuntime -> {
            this.returnValue
        }

        is ARoundRuntimeCallable -> {
            require(this.after == null) {
                "not after supported YET" //TODO main 可能返回一个任意值，不一定是 map，跟 after 的输入类型不一样。可能需要约定。
            }
            val beforeValue =
                ((this.before!!.runIt(args, logger)) as? Map<String, Any?>)?.toNoNullValueMap()
            logger?.debug("before run result - $beforeValue")
            if (!beforeValue.isNullOrEmpty()) {
                logger?.debug("run main callable")
                this.main.runIt(beforeValue, logger)
            } else null
        }

        else -> error("callable not support YET- $this")
    }

}
