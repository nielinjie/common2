package xyz.nietongxue.common.validate

import xyz.nietongxue.common.base.LogLevel
import xyz.nietongxue.common.base.Record

typealias ValidateResult<L> = Record<L>


fun <L> error(message: L): ValidateResult<L> = Record(message, level = LogLevel.ERROR)