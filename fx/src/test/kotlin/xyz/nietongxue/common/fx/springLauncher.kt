package xyz.nietongxue.common.fx

import jakarta.annotation.PreDestroy
import javafx.application.Application
import javafx.application.Platform
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import xyz.nietongxue.common.fx.HelloApplication


@SpringBootApplication
class Launcher : CommandLineRunner {

    override fun run(vararg args: String?) {
        // 在新线程启动 JavaFX，避免阻塞 Spring
        Thread {
            Application.launch(HelloApplication::class.java)
        }.apply {
            isDaemon = true
            start()
        }
    }

    @PreDestroy
    fun onContextClose() {
        // Spring 关闭时退出 JavaFX
        Platform.exit()
    }
}

fun main(args: Array<String>) {
    SpringApplicationBuilder(Launcher::class.java)
        .headless(false) // 允许 GUI
        .run(*args)
}