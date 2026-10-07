package xyz.nietongxue.common.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.treeToValue
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
import xyz.nietongxue.common.json.jo
import java.io.IOException
import java.nio.charset.StandardCharsets

object HttpCaller {

    @JvmOverloads
    fun call(
        url: String,
        inputs: Map<String, Any>,
        inputAsObject: ObjectNode = jo(),
        callOption: CallOption = CallOption(),
        logger: Logger?
    ): String {
        logger?.debug("ApiCaller.call：url={} inputs={} asObject = {} option={}", url, inputs, inputAsObject, callOption)

        // header
        val headers = mutableMapOf<String, Any>()
        val query = mutableMapOf<String, Any>()
        val body = mutableMapOf<String, Any>()
        val form = mutableMapOf<String, Any>()


        val allInputs: Map<String, Any> = inputs + (defaultOM.treeToValue<Map<String, Any>>(inputAsObject))

        allInputs.forEach { (key, value) ->
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
                if (body.isNotEmpty() ) {
                    val jsonBody = defaultOM.valueToTree<ObjectNode>(body)
                    httpPost.entity = StringEntity(jsonBody.toString(), StandardCharsets.UTF_8)
                    httpPost.addHeader("Content-Type", "application/json; charset=utf-8")
                } else if (form.isNotEmpty()) {
                    httpPost.entity = buildMultipartEntity(form, logger)
                    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                }
                request = httpPost
            }

            else -> error("Unsupported method: ${callOption.method}")
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