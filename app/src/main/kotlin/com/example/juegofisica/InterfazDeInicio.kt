package com.example.juegofisica

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

/**
 * Clase principal de la aplicación JavaFX.
 * Extiende Application y configura la ventana inicial del juego educativo de física.
 */
class InterfazDeInicio : Application() {

    override fun start(primaryStage: Stage) {
        // Contenedor principal: VBox centrado con espaciado y padding
        val root = VBox(20.0).apply {
            alignment = Pos.CENTER
            padding = Insets(20.0)
            styleClass.add("root-container")
        }

        // Sección del encabezado (título + subtítulo)
        val headerSection = HeaderSection()
        root.children.add(headerSection.construir())

        // Sección de botones del menú
        val menuButtonsSection = MenuButtonsSection()
        root.children.add(menuButtonsSection.construir())

        // Crear escena con dimensiones 400x300
        val scene = Scene(root, 400.0, 300.0)

        // Configurar y mostrar la ventana principal
        primaryStage.title = "Juego Educativo de Física"
        primaryStage.scene = scene
        primaryStage.show()
    }
}
