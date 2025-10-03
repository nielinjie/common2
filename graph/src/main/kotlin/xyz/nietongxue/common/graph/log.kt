package xyz.nietongxue.common.graph

import xyz.nietongxue.common.base.Record


interface GraphLogData {

}
data class StringLogData(val s: String) : GraphLogData
fun String.log() = Record(StringLogData(this))