package xyz.nietongxue.common.http

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

class HttpCallerTest {

    private lateinit var server: HttpServer
    private var port: Int = 0

    @BeforeEach
    fun setUp() {
        server = HttpServer.create(InetSocketAddress(0), 0)
        port = server.address.port
    }

    @AfterEach
    fun tearDown() {
        server.stop(0)
    }

    @Test
    fun `call with GET method and default mapping should append params to query string`() {
        var capturedQuery: String? = null
        server.createContext("/test") { exchange: HttpExchange ->
            capturedQuery = exchange.requestURI.query
            val response = "{\"result\":\"ok\"}"
            exchange.sendResponseHeaders(200, response.length.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }
        server.start()

        val url = "http://localhost:$port/test"
        val inputs = mapOf("name" to "alice", "age" to 30)
        val option = CallOption(method = RequestMethod.GET)

        val result = HttpCaller.call(url, inputs, option, null)

        assertEquals("{\"result\":\"ok\"}", result)
        assertTrue(capturedQuery!!.contains("name=alice"))
        assertTrue(capturedQuery!!.contains("age=30"))
    }

    @Test
    fun `call with POST method and body mapping should send json body`() {
        var capturedBody: String? = null
        var contentType: String? = null
        server.createContext("/test") { exchange: HttpExchange ->
            contentType = exchange.requestHeaders.getFirst("Content-Type")
            capturedBody = exchange.requestBody.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            val response = "{\"result\":\"created\"}"
            exchange.sendResponseHeaders(201, response.length.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }
        server.start()

        val url = "http://localhost:$port/test"
        val inputs = mapOf("title" to "test", "count" to 5)
        val option = CallOption(
            inputMapping = InputMapping(default = InputToPlace.Body),
            method = RequestMethod.POST
        )

        val result = HttpCaller.call(url, inputs, option, null)

        assertEquals("{\"result\":\"created\"}", result)
        assertTrue(capturedBody!!.contains("\"title\":\"test\""))
        assertTrue(capturedBody!!.contains("\"count\":5"))
        assertTrue(contentType!!.contains("application/json"))
    }

    @Test
    fun `call with custom header should include header in request`() {
        var capturedHeaders = mutableMapOf<String, String>()
        server.createContext("/test") { exchange: HttpExchange ->
            capturedHeaders["X-Custom-Header"] = exchange.requestHeaders.getFirst("X-Custom-Header")
            val response = "{\"result\":\"ok\"}"
            exchange.sendResponseHeaders(200, response.length.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }
        server.start()

        val url = "http://localhost:$port/test"
        val inputs = mapOf("X-Custom-Header" to "custom-value")
        val option = CallOption(
            inputMapping = InputMapping(
                specialFields = mapOf(InputToPlace.Header to listOf("X-Custom-Header")),
                default = InputToPlace.Query
            )
        )

        val result = HttpCaller.call(url, inputs, option, null)

        assertEquals("{\"result\":\"ok\"}", result)
        assertEquals("custom-value", capturedHeaders["X-Custom-Header"])
    }

    @Test
    fun `call with HeaderBear should set Authorization header with Bearer prefix`() {
        var authHeader: String? = null
        server.createContext("/test") { exchange: HttpExchange ->
            authHeader = exchange.requestHeaders.getFirst("Authorization")
            val response = "{\"result\":\"ok\"}"
            exchange.sendResponseHeaders(200, response.length.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }
        server.start()

        val url = "http://localhost:$port/test"
        val inputs = mapOf("token" to "my-secret-token")
        val option = CallOption(
            inputMapping = InputMapping(
                specialFields = mapOf(InputToPlace.HeaderBear to listOf("token")),
                default = InputToPlace.Query
            )
        )

        val result = HttpCaller.call(url, inputs, option, null)

        assertEquals("{\"result\":\"ok\"}", result)
        assertEquals("Bearer my-secret-token", authHeader)
    }

    @Test
    fun `call should throw IOException when response status code is 400 or greater`() {
        server.createContext("/test") { exchange: HttpExchange ->
            val response = "Bad Request"
            exchange.sendResponseHeaders(400, response.length.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }
        server.start()

        val url = "http://localhost:$port/test"
        val inputs = mapOf<String, Any>()
        val option = CallOption(method = RequestMethod.GET)

        val exception = assertThrows<IOException> {
            HttpCaller.call(url, inputs, option, null)
        }

        assertTrue(exception.message!!.contains("400"))
    }

    @Test
    fun `call with POST and form data should send multipart form`() {
        var capturedContentType: String? = null
        var capturedBody: String? = null
        server.createContext("/test") { exchange: HttpExchange ->
            capturedContentType = exchange.requestHeaders.getFirst("Content-Type")
            capturedBody = exchange.requestBody.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            val response = "{\"result\":\"uploaded\"}"
            exchange.sendResponseHeaders(200, response.length.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }
        server.start()

        val url = "http://localhost:$port/test"
        val inputs = mapOf("fileName" to "test.txt", "content" to "hello")
        val option = CallOption(
            inputMapping = InputMapping(default = InputToPlace.Form),
            method = RequestMethod.POST
        )

        val result = HttpCaller.call(url, inputs, option, null)

        assertEquals("{\"result\":\"uploaded\"}", result)
        assertTrue(capturedContentType!!.contains("application/x-www-form-urlencoded"))
    }
}
