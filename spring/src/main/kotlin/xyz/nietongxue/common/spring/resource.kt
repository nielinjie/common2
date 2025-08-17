package xyz.nietongxue.common.spring

import org.springframework.core.io.Resource
import java.nio.charset.Charset

inline fun <reified S> Resource.content(): S {
    return when (S::class) {
        String::class -> this.getContentAsString(Charset.defaultCharset()) as S
        ByteArray::class -> this.contentAsByteArray as S
        else -> throw IllegalArgumentException("unsupported type ${S::class}")
    }
}