package xyz.nietongxue.common.schema.jsonSchema

import com.fasterxml.jackson.databind.node.ObjectNode
import io.swagger.v3.core.util.AnnotationsUtils
import io.swagger.v3.core.util.Json31
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.annotations.media.Schema as AS


/*
## type 关键字支持的类型：

type关键字指定数据必须符合的类型。它可以是以下之一或一个包含多个类型的数组：
string - 字符串
number - 数字（整数或小数）
integer - 整数
boolean - 布尔值
object - 对象
array - 数组
null - null 值
## 示例：

{ "type": "string" }
{ "type": ["string", "null"] }  // 允许字符串或null

##format 关键字支持的常用格式（部分）：

format关键字用于对特定类型的字符串数据进行更细致的格式验证。它依赖于验证器的实现，但 JSON Schema 规范定义了一些常用格式：
date - 完整的日期，如 2024-12-31
time - 时间，如 14:30:00
date-time - 日期和时间，如 2024-12-31T14:30:00Z
duration - 时间间隔，如 P1D（一天）
email - 电子邮件地址
idn-email - 国际化电子邮件地址
hostname - 主机名
idn-hostname - 国际化主机名
ipv4 - IPv4 地址
ipv6 - IPv6 地址
uuid - UUID
uri - URI
uri-reference - URI 引用
iri - 国际化资源标识符
iri-reference - 国际化资源标识符引用
uri-template - URI 模板
json-pointer - JSON 指针
relative-json-pointer - 相对 JSON 指针
regex - 正则表达式
 */


fun <T : Any> Schema<T>.toJsonSchema(): ObjectNode {
    return Json31.mapper().valueToTree(this)
}

fun AS.toSchemaO(): Schema<Any> {
    return AnnotationsUtils.getSchemaFromAnnotation(this, null).get()
}