package xyz.nietongxue.common.http

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.ByteArrayBody
import org.apache.http.entity.mime.content.ContentBody
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import xyz.nietongxue.common.json.defaultOM
import java.io.IOException
import java.nio.charset.StandardCharsets

object HttpCaller {

    private val om = defaultOM

    //TODO 有时候，输入参数被转换成 map 会有变形，比如tools 调用传入 json string，转换成 map 时会被扁平。
    // 不全是这里的问题，大模型的 inputString 总是在 property 的某一层加上“”，使其不是一个 object，而是一个 json string。
    // 没搞清楚是什么原因。大模型tool 机制不允许整个 inputString 是个深层json object？
    // 感觉各种没道理，是 json schema 没写对？当时的 jsonSchema 是-
    // propertyDeclares 总是要包在 string 里面。
    // "inputJsonSchema":"{
    //          \"type\":[\"object\",\"null\"],
    //          \"additionalProperties\":false,
    //          \"properties\":{\"name\":{\"type\":\"string\"},\"namespace\":{\"type\":\"string\"},\"idName\":{\"type\":\"string\",\"description\":\"The name of the id property.\"},
    //              \"propertyDeclares\":{\"type\":[\"object\",\"null\"],\"additionalProperties\":true,\"description\":\"The property declares of the entity, a object, NOT a jsonString, example - {name: string, age: number}\"}},\"required\":[\"name\",\"namespace\",\"idName\"]}"}


    @JvmOverloads
    fun call(url: String, inputs: Map<String, Any>, callOption: CallOption = CallOption(), logger: Logger?): String {
        logger?.debug("ApiCaller.call：url={} inputs={} option={}", url, inputs, callOption)

        // header
        val headers = mutableMapOf<String, Any>()
        val query = mutableMapOf<String, Any>()
        val body = mutableMapOf<String, Any>()
        val form = mutableMapOf<String, Any>()


        inputs.forEach { (key, value) ->
            val place = callOption.inputMapping.getByField(key)
            when (place) {
                InputToPlace.Header -> headers[key] = value
                InputToPlace.Body -> body[key] = value
                InputToPlace.Query -> query[key] = value
                InputToPlace.Form -> form[key] = value
                InputToPlace.HeaderBear -> headers["Authorization"] = "Bearer $value"
                else -> TODO()
            }
        }


        val httpClient = HttpClients.createDefault()
        val request: HttpRequestBase
        val uriBuilder = URIBuilder(url)
        query.forEach { (k, v) ->
            val paramValue = v.toString()
            uriBuilder.addParameter(k, paramValue)
        }
        when (callOption.method) {
            RequestMethod.GET -> {
                request = HttpGet(uriBuilder.build())
            }
            RequestMethod.POST -> {
                val httpPost = HttpPost(uriBuilder.build())
                if (body.isNotEmpty()) {
                    val jsonBody = om.writeValueAsString(body)
                    httpPost.entity = StringEntity(jsonBody, StandardCharsets.UTF_8)
                    httpPost.addHeader("Content-Type", "application/json; charset=utf-8")
                } else if (
                    form.isNotEmpty()
                ) {
                    httpPost.entity = buildMultipartEntity(form, logger)
                    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                }
                request = httpPost
            }
        }
        headers.forEach { (string, any) -> request.addHeader(string, any.toString()) }


//        request.config = RequestConfig.custom().setConnectTimeout(30 * 1000).setSocketTimeout(30 * 1000).build()

        // 注入 Header

        // 执行请求并处理返回
        logger?.debug("API请求 - request={}", request)
        httpClient.execute(request).use { response ->
            val statusCode = response.statusLine.statusCode
            val entity = response.entity
            val result = entity?.let { EntityUtils.toString(it, StandardCharsets.UTF_8) } ?: ""
            logger?.debug("API响应 - statusCode={} response={}", statusCode, result)

            if (statusCode >= 300) {
                throw IOException("API请求失败: code - $statusCode, response - $response")
            }
            return result
        }
    }

    private fun buildMultipartEntity(inputs: Map<String, Any>, logger: Logger?): org.apache.http.HttpEntity {
        val entityBuilder = MultipartEntityBuilder.create().setCharset(StandardCharsets.UTF_8)
        for ((key, value) in inputs) {
            val contentBody = createContentBody(key, value, logger)
            entityBuilder.addPart(key, contentBody)
        }
        return entityBuilder.build()
    }


    private fun createContentBody(key: String?, value: Any, logger: Logger?): ContentBody {
        logger?.debug("处理multipart参数 - key={} value={} valueType={}", key, value, value.javaClass.getSimpleName())
        when (value) {
            is String -> {
                return StringBody(value, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8))
            }

            is Number -> {
                return StringBody(value.toString(), ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8))
            }

            is Boolean -> {
                return StringBody(value.toString(), ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8))
            }

            is java.io.File -> {
                return FileBody(value, ContentType.DEFAULT_BINARY, value.getName())
            }

            is ByteArray -> {
                return ByteArrayBody(value, ContentType.DEFAULT_BINARY, key)
            }

            else -> {
                // 复杂对象序列化为JSON
                val json = defaultOM.writeValueAsString(value)
                logger?.debug("序列化复杂对象: {} -> {}", key, json)
                return StringBody(json, ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8))
            }
        }
    }

}