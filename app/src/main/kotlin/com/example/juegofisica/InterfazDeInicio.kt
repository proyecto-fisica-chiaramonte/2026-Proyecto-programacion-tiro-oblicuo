package com.example.juegofisica

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

/**
 * Pantalla de inicio del juego educativo de física.
 * Muestra el encabezado de bienvenida y el acceso al nivel MRUV.
 * Implementa la navegación cambiando la scene del primaryStage.
 */
class InterfazDeInicio : Application() {

    /** Referencia al stage principal para controlar la navegación entre pantallas. */
    private lateinit var primaryStage: Stage

    /** Referencia a la escena del menú principal para poder volver a ella. */
    private lateinit var menuScene: Scene

    override fun start(stage: Stage) {
        primaryStage = stage
        // Crear y guardar la escena del menú
        menuScene = crearEscenaMenu()
        primaryStage.scene = menuScene
        primaryStage.title = "Juego Educativo de Física"
        primaryStage.show()
    }

    /**
     * Crea y devuelve la Scene del menú principal con el encabezado y el botón de inicio.
     * Se reutiliza tanto al iniciar la app como al volver desde pantallas secundarias.
     */
    private fun crearEscenaMenu(): Scene {
        // Contenedor principal con espaciado y alineación centrada
        val root = VBox(20.0).apply {
            alignment = Pos.CENTER
            padding = Insets(20.0)
            styleClass.add("root-container")
        }

        // Sección del encabezado con título y subtítulo
        val headerSection = HeaderSection()
        root.children.add(headerSection.construir())

        // Sección de botones con navegación a MRUV
        val menuButtonsSection = MenuButtonsSection(
            onMRUV = {
                // Ir a la pantalla de explicación del nivel MRUV
                val pantallaExplicacion = PantallaExplicacionMRUV(primaryStage, menuScene)
                primaryStage.scene = pantallaExplicacion.crearEscena()
                primaryStage.title = "Instrucciones MRUV"
            }
        )
        root.children.add(menuButtonsSection.construir())

        return Scene(root, 400.0, 300.0)
    }
}