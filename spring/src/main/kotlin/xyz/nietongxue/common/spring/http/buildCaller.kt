package xyz.nietongxue.common.spring.http

import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.core.util.AnnotationsUtils
import io.swagger.v3.oas.models.media.Schema
import org.springframework.web.bind.annotation.*
import xyz.nietongxue.common.collections.oneOrNone
import xyz.nietongxue.common.http.CallOption
import xyz.nietongxue.common.http.InputMapping
import xyz.nietongxue.common.http.InputToPlace
import xyz.nietongxue.common.http.RequestMethod
import java.lang.reflect.Method
import java.net.URI
import io.swagger.v3.oas.annotations.media.Schema as SwaggerSchema

/**
 * 根据 Spring Controller 方法生成 HTTP 调用
 */
data class Calling(
    val schema: Schema<*>,
    val option: CallOption,
    val url: URI,
    val name: String? = null,
    val description: String? = null,
)

fun buildCaller(method: Method): Calling {

    val paras = parametersAndPlace(method)
    val (httpMethod, fullPath) = extractMappingInfo(method)
    var option = CallOption(
        method = when (httpMethod.uppercase()) {
            "GET" -> RequestMethod.GET
            "POST" -> RequestMethod.POST
            "PUT" -> RequestMethod.PUT
            "DELETE" -> RequestMethod.DELETE
            "PATCH" -> RequestMethod.PATCH
            else -> RequestMethod.POST
        }
    )
    var mergedSchema: Schema<*> = Schema<Any>().apply { type = "object" }

    val (body, not) = paras.partition {
        it.place == InputToPlace.Body
    }
    body.oneOrNone()?.also {
        mergedSchema = it.schema
        option = option.copy(inputMapping = InputMapping.inBody)
    }
    not.forEach { (name, schema, place) ->
        mergedSchema.addProperty(name, schema)
        option = option.copy(inputMapping = option.inputMapping.setPlace(place, name))
    }
    return Calling(
        mergedSchema,
        option,
        URI(fullPath),
        name = method.name,
        description = method.getAnnotation(SwaggerSchema::class.java)?.description
    )
}

data class ParameterWithPlace(val name: String, val schema: Schema<*>, val place: InputToPlace)

fun parametersAndPlace(method: Method): List<ParameterWithPlace> {
    val parameters = method.parameters
    val re = mutableListOf<ParameterWithPlace>()
    parameters.firstOrNull()?.also {
        if (it.name == "arg0") {
            error("没有找到参数名，使用 -java-parameters 方案。")
        }
    }
    val placeAnnoClasses = listOf(
        RequestBody::class.java,
        RequestParam::class.java,
        PathVariable::class.java,
        CookieValue::class.java
    )
    parameters.forEach { para ->
        val ans = para.annotations.toList()
        val placeAnno = ans.oneOrNone { it.javaClass in placeAnnoClasses }
        placeAnno?.also { annotation -> //这里应该只有一次，不应该 foreach。annotations 可能有几个，但这里的几个分支，只能有互斥的一次。
            re.add(
                ParameterWithPlace(
                    para.name!!,
                    typeToSchema(para.type).mergeSchemaAnnotation(para.getAnnotation(SwaggerSchema::class.java)),
                    when (annotation) {
                        is RequestBody -> InputToPlace.Body
                        is RequestParam -> InputToPlace.Query
                        is PathVariable -> InputToPlace.Path
                        is CookieValue -> InputToPlace.Cookie
                        else -> error("unknown annotation")
                    }
                )
            )
        }
    }
    return re
}

private fun typeToSchema(javaClass: Class<*>): Schema<*> {
    return when (javaClass) {
        String::class.java -> Schema<String>().apply { type = "string" }
        Integer::class.java, Integer.TYPE ->
            Schema<Int>().apply { type = "integer"; format = "int32" }

        java.lang.Long::class.java, java.lang.Long.TYPE ->
            Schema<Long>().apply { type = "integer"; format = "int64" }

        java.lang.Boolean::class.java, java.lang.Boolean.TYPE ->
            Schema<Boolean>().apply { type = "boolean" }

        java.lang.Double::class.java, java.lang.Double.TYPE ->
            Schema<Double>().apply { type = "number"; format = "double" }

        java.lang.Float::class.java, java.lang.Float.TYPE ->
            Schema<Float>().apply { type = "number"; format = "float" }

        else -> {
            ModelConverters.getInstance().readAllAsResolvedSchema(javaClass)?.schema?.also {
                println(it)
            } ?: error("no schema find - $javaClass")
        }
    }
}

/**
 * 将 @Schema 注解的属性合并到已有的 Swagger Schema 对象上
 *
 * 使用 swagger-core 自带的 AnnotationsUtils.getSchemaFromAnnotation 将注解
 * 完整转换为 Schema 模型，再合并非空属性到目标 Schema 中。
 */

private fun <T : Any> Schema<T>.mergeSchemaAnnotation(annotation: SwaggerSchema?): Schema<T> {
    if (annotation == null) return this
    val annotationSchema = AnnotationsUtils.getSchemaFromAnnotation(annotation, null)
        .orElse(null) ?: return this

    // 注解 Schema 的属性优先，但保留原有的 type/format 作为 fallback
    val originalType = this.type
    val originalFormat = this.format

    annotationSchema.type?.also { this.type = it }
    annotationSchema.format?.also { this.format = it }
    if (this.type == null) this.type = originalType
    if (this.format == null) this.format = originalFormat

    annotationSchema.description?.also { this.description = it }
    annotationSchema.default?.also { this.setDefault(it) }
    annotationSchema.example?.also { this.example = it }
    annotationSchema.pattern?.also { this.pattern = it }
    annotationSchema.maximum?.also { this.maximum = it }
    annotationSchema.minimum?.also { this.minimum = it }
    annotationSchema.maxLength?.also { this.maxLength = it }
    annotationSchema.minLength?.also { this.minLength = it }
    annotationSchema.nullable?.also { this.nullable = it }
    annotationSchema.deprecated?.also { this.deprecated = it }
    annotationSchema.enum?.also { @Suppress("UNCHECKED_CAST") this.setEnum(it as List<T>) }
    return this
}

fun extractMethodMapping(method: Method): Pair<String, String> {
    // GetMapping
    method.getAnnotation(GetMapping::class.java)?.let {
        val path: String = it.value.firstOrNull()
            ?: throw IllegalArgumentException("GetMapping value 为空")
        return "GET" to path
    }
    // PostMapping
    method.getAnnotation(PostMapping::class.java)?.let {
        val path: String = it.value.firstOrNull()
            ?: throw IllegalArgumentException("PostMapping value 为空")
        return "POST" to path
    }
    // PutMapping
    method.getAnnotation(PutMapping::class.java)?.let {
        val path: String = it.value.firstOrNull()
            ?: throw IllegalArgumentException("PutMapping value 为空")
        return "PUT" to path
    }
    // DeleteMapping
    method.getAnnotation(DeleteMapping::class.java)?.let {
        val path: String = it.value.firstOrNull()
            ?: throw IllegalArgumentException("DeleteMapping value 为空")
        return "DELETE" to path
    }
    // PatchMapping
    method.getAnnotation(PatchMapping::class.java)?.let {
        val path: String = it.value.firstOrNull()
            ?: throw IllegalArgumentException("PatchMapping value 为空")
        return "PATCH" to path
    }
    // RequestMapping
    method.getAnnotation(RequestMapping::class.java)?.let {
        val methodValue = it.method.firstOrNull()?.name ?: "GET"
        val path: String = it.value.firstOrNull()
            ?: throw IllegalArgumentException("RequestMapping value 为空")
        return methodValue to path
    }

    throw IllegalArgumentException("方法上没有找到支持的 Spring Mapping 注解: ${method.name}")
}

fun extractMappingInfo(method: Method): Pair<String, String> {
    // 提取类级别的路径前缀
    val classPathPrefix = method.declaringClass.getAnnotation(RequestMapping::class.java)
        ?.value?.firstOrNull()?.trimEnd('/') ?: ""

    // 提取方法级别的 Mapping 注解
    val (httpMethod, methodPath) = extractMethodMapping(method)

    // 合并类路径和方法路径
    val fullPath = if (classPathPrefix.isEmpty()) {
        methodPath
    } else {
        "$classPathPrefix/${methodPath.trimStart('/')}"
    }

    return httpMethod to fullPath
}

