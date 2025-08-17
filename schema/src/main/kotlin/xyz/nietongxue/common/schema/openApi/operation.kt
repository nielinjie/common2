package xyz.nietongxue.common.schema.openApi

import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.parameters.Parameter.StyleEnum.*
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import xyz.nietongxue.common.schema.Operation
import io.swagger.v3.oas.models.Operation as SO
import io.swagger.v3.oas.models.parameters.Parameter as SP

fun Operation.toOpenApi(): SO {
    val paras: List<SP> = this.parameters.map {
        SP().apply {
            name = it.name
            schema = it.schema.toOpenApi()
            required = it.required
            `in` = it.`in`
            style = when (it.style) {
                "form" -> FORM
                "simple" -> SIMPLE
                "spaceDelimited" -> SPACEDELIMITED
                "pipeDelimited" -> PIPEDELIMITED
                "deepObject" -> DEEPOBJECT
                else -> error("not supported style")
            }
        }
    }
    val request: RequestBody? = this.request?.let {
        RequestBody().apply {
            content = Content().apply {
                val mediaType = MediaType().apply {
                    schema = it.toOpenApi()
                }
                this["application/json"] = mediaType
            }
        }
    }
    val response =
        ApiResponses().apply {
            this["200"] = this@toOpenApi.response?.let {
                ApiResponse().apply {
                    description = "OK"
                    content = Content().apply {
                        val mediaType = MediaType().apply {
                            schema = it.toOpenApi()
                        }
                        this["application/json"] = mediaType
                    }
                }
            } ?: ApiResponse().apply {
                description = "no return"
            }
        }
    return SO().apply {
        requestBody = request
        responses = response
        parameters = paras
    }
}