package xyz.nietongxue.common.taskdag.stringEvent

import xyz.nietongxue.common.taskdag.Context
import xyz.nietongxue.common.taskdag.Task

abstract class AbstractTask : Task<String> {
    val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
    override fun exception(context: Context, e: Exception): String {
        logger.error("exception", e)
        return CommonEvents.EXCEPTION
    }
}