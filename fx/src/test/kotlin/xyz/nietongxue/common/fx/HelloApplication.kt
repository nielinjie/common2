package xyz.nietongxue.common.fx

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage


class HelloApplicationFXML : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(HelloApplicationFXML::class.java.getResource("hello-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 320.0, 240.0)
        stage.title = "Hello!"
        stage.scene = scene
        stage.show()
    }
}

class HelloApplication : Application() {


    override fun start(stage: Stage) {
        val welcomeText = Label()
        val helloButton = Button("Hello!")

        helloButton.setOnAction {
            welcomeText.text = "Welcome to JavaFX Application!"
        }

        val root = VBox().apply {
            add(welcomeText)
            add(helloButton)
            spacing = 10.0
            alignment = Pos.CENTER
            padding = Insets(20.0, 20.0, 20.0, 20.0)
        }
        with(stage) {
            title = "Hello!"
            scene = Scene(root, 320.0, 240.0)
            setOnCloseRequest {
                this.close() // 只关闭当前窗口
            }
            stage.show()
        }
    }

}
  
