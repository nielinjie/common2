package xyz.nietongxue.common.processing

import xyz.nietongxue.common.log.Log
import xyz.nietongxue.common.base.Record

typealias StopResult<L> = Record<L>
typealias ProcessingWithLog<V,L> = Processing<Log<L>, StopResult<L>, V>