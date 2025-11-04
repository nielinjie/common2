package xyz.nietongxue.common.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.thisptr.jackson.jq.JsonQuery
import net.thisptr.jackson.jq.Scope
import net.thisptr.jackson.jq.Versions

const val JQ_TRANSFER = "jqTransfer"
const val JQ_TRANSFER_FIRST = "jqTransferFirst"
fun transform(data: JsonNode, transfer: String, transferType: String): JsonNode {
    require(transferType in listOf(JQ_TRANSFER, JQ_TRANSFER_FIRST))
    val rootScope: Scope = Scope.newEmptyScope()

    val om = jacksonObjectMapper()
    val q = JsonQuery.compile(transfer, Versions.JQ_1_7)
    val input = (data)
    val out: MutableList<JsonNode> = mutableListOf()
    q.apply(rootScope, input, out::add)
    return om.valueToTree(if (transferType == JQ_TRANSFER) out else out.firstOrNull())
}

fun <K, V> Map<K, V>.transform(
    transfer: String,
    transferType: String,
    om: ObjectMapper = jacksonObjectMapper()
): Any {
    return (transform(om.valueToTree(this), transfer, transferType) as JsonNode).let {
        om.treeToValue(it, Any::class.java)
    }
}