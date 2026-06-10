package xyz.nietongxue.common.sql

enum class Dialects {
    MySql,
    Postgres,
    Doris,
    Oracle;

    companion object {
        fun fromName(value: String): Dialects {
            return when (value.uppercase()) {
                "MYSQL" -> MySql
                "POSTGRES" -> Postgres
                "DORIS" -> Doris
                "ORACLE" -> Oracle
                else -> error("not supported")
            }
        }
    }

}
