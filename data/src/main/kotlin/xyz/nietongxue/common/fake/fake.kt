package xyz.nietongxue.common.fake

import net.datafaker.Faker
import xyz.nietongxue.common.base.randomId
import xyz.nietongxue.common.schema.*
import java.time.ZoneId
import java.util.*

val defaultFaker = Faker(Locale.CHINA)

fun DataSchema.fake(faker: Faker = defaultFaker): Any? {
    return when (this) {
        is PrimitiveSchema -> when (this.typeName()) {
            "string" -> when (val lengthRange = this.constraints.lengthRange()) {
                null -> faker.text().text()
                else -> faker.text().text(lengthRange.min ?: 0, lengthRange.max ?: 100)
            }

            "uuid" -> randomId()
            "date" -> faker.timeAndDate().past().atZone(ZoneId.systemDefault()).toLocalDate()
            "datetime" -> faker.timeAndDate().past().atZone(ZoneId.systemDefault()).toLocalDateTime()
            "time" -> faker.timeAndDate().past().atZone(ZoneId.systemDefault()).toLocalTime()
            "number" -> faker.number().randomDouble(2, 0, 100)
            "boolean" -> faker.bool().bool()
            "int" -> faker.number().randomDigit()
            else -> throw IllegalArgumentException("unknown type ${this.typeName()}")
        }

        is ObjectSchema -> {
            val obj = mutableMapOf<String, Any?>()
            this.properties.forEach {
                obj[it.key] = it.value.fake(faker)
            } //TODO obey additional properties。
            obj
        }

        is ArraySchema -> {
            val list = mutableListOf<Any?>()
            for (i in 0 until 3) { //TODO obey list constraints，length range。
                list.add(this.itemSchema.fake(faker))
            }
            list
        }

        else -> error("unknown schema type")
    }
}