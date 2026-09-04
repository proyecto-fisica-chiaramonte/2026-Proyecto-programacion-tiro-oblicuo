package com.example.juegofisica

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

/**
 * Pantalla de inicio del juego educativo de física.
 * Muestra el encabezado de bienvenida y los botones de selección de juego.
 * Implementa la navegación cambiando la scene del primaryStage.
 */
class InterfazDeInicio : Application() {

    /** Referencia al stage principal para controlar la navegación entre pantallas. */
    private lateinit var primaryStage: Stage

    override fun start(stage: Stage) {
        primaryStage = stage
        mostrarMenuPrincipal()
        primaryStage.title = "Juego Educativo de Física"
        primaryStage.show()
    }

    /**
     * Restaura la escena del menú principal en el stage.
     * Las pantallas secundarias invocan este método al presionar "Volver atrás".
     */
    fun mostrarMenuPrincipal() {
        primaryStage.scene = crearEscenaMenu()
    }

    /**
     * Crea y devuelve la Scene del menú principal con el encabezado y los botones.
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

        // Sección de botones con lambdas de navegación
        val menuButtonsSection = MenuButtonsSection(
            onTiroOblicuo = {
                val pantallaTiro = PantallaTiroOblicuo(
                    onVolver = { mostrarMenuPrincipal() }
                )
                primaryStage.scene = pantallaTiro.crearEscena()
                primaryStage.title = "Tiro Oblicuo"
            },
            onMRUV = {
                val pantallaMruv = PantallaMRUV(
                    onVolver = { mostrarMenuPrincipal() }
                )
                primaryStage.scene = pantallaMruv.crearEscena()
                primaryStage.title = "MRUV"
            }
        )
        root.children.add(menuButtonsSection.construir())

        return Scene(root, 400.0, 300.0)
    }
}
