package xyz.nietongxue.common.schema

interface CommonNamedTypes {
    object STRING : CommonNamedTypes {
        override val name: String = "string"
    }

    object NUMBER : CommonNamedTypes {
        override val name: String = "number"
    }

    object BOOLEAN : CommonNamedTypes {
        override val name: String = "boolean"
    }

    object ANY : CommonNamedTypes {
        override val name: String = "any"
    }

    object INT : CommonNamedTypes {
        override val name: String = "int"
    }

    object LONG : CommonNamedTypes {
        override val name: String = "long"
    }

    object UUID : CommonNamedTypes {
        override val name: String = "uuid"
    }

    object DATETIME : CommonNamedTypes {
        override val name: String = "datetime"
    }

    object TIMESTAMP : CommonNamedTypes {
        override val name: String = "timestamp"
    }

    val name: String
}
