package com.example.juegofisica

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

/**
 * Pantalla de inicio del juego educativo de física (SRP).
 */
class InterfazDeInicio : Application() {

    /** Referencia al stage principal para controlar la navegación entre pantallas. */
    private lateinit var primaryStage: Stage

    /** Referencia a la escena del menú principal para poder volver a ella. */
    private lateinit var menuScene: Scene

    override fun start(stage: Stage) {
        primaryStage = stage
        menuScene = crearEscenaMenu()
        primaryStage.scene = menuScene
        primaryStage.title = "Juego Educativo de Física"
        primaryStage.show()
    }

    private fun crearEscenaMenu(): Scene {
        val botones = MenuButtonsSection(
            onMRUV = { abrirPantalla(PantallaDificultadMRUV(primaryStage, menuScene), "Dificultad MRUV") },
            onTiroOblicuo = { abrirPantalla(PantallaSimulacionTiroOblicuo(primaryStage, menuScene), "Simulación de Tiro Oblicuo") }
        )
        val root = VBox(20.0, HeaderSection().construir(), botones.construir()).apply {
            alignment = Pos.CENTER
            padding = Insets(20.0)
            styleClass.add("root-container")
        }
        return Scene(root, 400.0, 300.0)
    }

    private fun abrirPantalla(pantalla: PantallaJuego, titulo: String) {
        primaryStage.scene = pantalla.crearEscena()
        primaryStage.title = titulo
    }
}
