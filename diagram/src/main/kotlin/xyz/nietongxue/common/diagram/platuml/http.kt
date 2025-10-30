package xyz.nietongxue.common.diagram.platuml

import org.springframework.http.MediaType
import org.springframework.web.client.RestClient


/*
以外部server完成绘图。比如docker
不依赖于本地的plantuml和graphviz
 */

/*
POST /uml/png/ HTTP/1.1
Host: www.plantuml.com
Content-Type: text/plain

@startuml
Alice -> Bob: 测试
@enduml
 */


class PlantUmlClient(url: String = "http://localhost:8888") {

    private val restClient = RestClient.builder()
        .baseUrl(url)
        .build()

    fun image(plantUmlCode: String, outputFormat: String = "png"): ByteArray {
        return restClient.post()
            .uri("/$outputFormat/")
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.IMAGE_PNG)
            .body(plantUmlCode)
            .retrieve()
            .body(ByteArray::class.java) ?: throw RuntimeException("Failed to generate PNG")
    }
}