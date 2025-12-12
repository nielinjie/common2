package xyz.nietongxue.common.json.autoParse

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import xyz.nietongxue.common.string.cList


/**
 * TODO 搬到 json 的 common util 里面去。
 */

interface Format {
    fun json(string: String): JsonNode
    fun shortList(string: String): List<String> {
        return string.split(",")
    }
}


/*
声明样式：
1. string/number/boolean
2. [string]
2. { "propertyA": string }
2. { "propertyA": { "pc": string, "mobile": string } }
3. { "_": string }
3. { "propertyA": [string] }
4. { "propertyA": [string], "propertyB": number }
5. { "propertyA": [string], "propertyB": {"pc": ....}}
6. string/max(5)/notBlank
7. string/default(xxx) TODO 比较难，往后排。default 是一个转换，而不是一个校验
 */

/*
https://www.relaxedjson.org
 */
object RJsonFormat : Format {
    override fun json(string: String): JsonNode {
//        <dependency>
//        <groupId>tv.twelvetone.rjson</groupId>
//        <artifactId>rjson</artifactId>
//        <version>1.3.1-SNAPSHOT</version>
//        </dependency>
        // 新的地方在 - https://maven-us.nuxeo.org/nexus/content/repositories/thirdparty-snapshots/tv/twelvetone/rjson/rjson/1.3.1-SNAPSHOT/
        // google 来的，不一定保险。
        return RJson.rjsonToJackson(RJson.parse(string))
    }

    override fun shortList(string: String): List<String> {
        return string.cList()
    }

}


object Json5 : Format {
    override fun json(string: String): JsonNode {
        /*
        ALLOW_UNQUOTED_FIELD_NAMES
        ALLOW_TRAILING_COMMA
        ALLOW_SINGLE_QUOTES
        ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER
        ALLOW_NON_NUMERIC_NUMBERS
        ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS
         */
        val om = jacksonObjectMapper().also {
            it.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            it.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
            it.enable(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            it.enable(JsonParser.Feature.ALLOW_TRAILING_COMMA)
            it.enable(JsonParser.Feature.ALLOW_COMMENTS)
            it.enable(JsonParser.Feature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS)
        }
        return om.readTree(string)
    }
}
/*
json
就是一般的 json。
 */

object NormalJson : Format {
    override fun json(string: String): JsonNode {
        return jacksonObjectMapper().readTree(string)
    }

}

object Yaml : Format {
    override fun json(string: String): JsonNode {
        return ObjectMapper(YAMLFactory()).registerModule(kotlinModule())
            .readTree(string)
    }
}
