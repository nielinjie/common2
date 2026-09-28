package xyz.nietongxue.common.spring.http

import io.swagger.v3.core.converter.ModelConverters
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.reflect.jvm.kotlinFunction


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AutoHttCallerTest {

    @Test
    fun testBuilding() {
        val method = AController::class.java.getMethod("allBody", Map::class.java)
        val (schema, callOption) = buildCaller(method)
        println(schema)
        println(callOption)
    }

    @Test
    fun testBuilding2() {
        val method = AController::class.java.getMethod("bodyAndPara", Body::class.java, String::class.java)
        val (schema, callOption) = buildCaller(method)
//        println(schema)
        println(callOption)
    }

    @Test
    fun getSchema() {
        ModelConverters.getInstance().readAllAsResolvedSchema(Body::class.java).schema.also {
            println(it)
        }
    }

    @Test
    fun getSchemaByPara() {
        val method = AController::class.java.getMethod("bodyAndPara", Body::class.java, String::class.java)
        val parameters = method.parameters
        parameters.first().also {
            it.name.also {
                println(it)
            }
            it.type.also {
                ModelConverters.getInstance().readAllAsResolvedSchema(it).schema.also {
                    println(it)
                }
            }
        }
    }

    @Test
    fun getSchemaByPara2() {
        val method = AController::class.java.getMethod("bodyAndPara", Body::class.java, String::class.java)
        val parameters = method.kotlinFunction!!.parameters
        parameters.first().also {
            it.name.also {
                println(it)
            }
            it.type.javaClass.also {
                ModelConverters.getInstance().readAllAsResolvedSchema(it).schema.also {
                    println(it)
                }
            }
        }
    }
}


class Body(var hello: String)


@RestController
@RequestMapping("/test")
class AController {

    @PostMapping("/allBody")
    fun allBody(@RequestBody body: Map<String, Any>): String {
        return body["hello"] as String
    }


    @PostMapping("/bodyAndPara")
    fun bodyAndPara(@RequestBody body: Body, @RequestParam name: String): String {
        return name + body.hello
    }
}