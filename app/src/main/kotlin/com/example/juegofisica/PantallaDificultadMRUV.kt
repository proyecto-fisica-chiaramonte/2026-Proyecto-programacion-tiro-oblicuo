package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage

/**
 * Pantalla para seleccionar la dificultad del nivel MRUV (Fácil o Difícil) (SRP / DRY).
 *
 * Reutiliza [ComponentesUI] para construir los botones con efectos de hover sin duplicar código.
 *
 * @property stage el Stage principal de la aplicación.
 * @property menuScene la Scene del menú principal a la que se regresa.
 */
class PantallaDificultadMRUV(
    private val stage: Stage,
    private val menuScene: Scene
) : PantallaJuego {

    /** Proveedor compartido de componentes de UI (DRY). */
    private val componentesUI: ComponentesUI = ComponentesUI()

    /**
     * Construye y devuelve la Scene de la pantalla de selección de dificultad.
     *
     * @return la Scene lista para asignarse al Stage principal.
     */
    override fun crearEscena(): Scene {
        val root = VBox(25.0).apply {
            alignment = Pos.CENTER
            padding = Insets(40.0)
            style = "-fx-background-color: #000000;"
        }

        val titulo = Text("Selecciona la Dificultad").apply {
            font = Font.font("System", FontWeight.BOLD, 24.0)
            fill = Color.WHITE
        }

        val contenedorDificultad = HBox(20.0, crearBotonFacil(), crearBotonDificil()).apply {
            alignment = Pos.CENTER
        }

        val btnVolver = componentesUI.crearBotonVolver {
            stage.scene = menuScene
            stage.title = "Juego Educativo de Física"
        }.apply {
            text = "Volver atrás"
        }

        root.children.addAll(titulo, contenedorDificultad, btnVolver)
        return Scene(root, 400.0, 300.0)
    }

    /**
     * Crea el botón para iniciar el modo Fácil de MRUV.
     */
    private fun crearBotonFacil(): Button {
        val normal = "-fx-background-color: #005500; -fx-text-fill: #00FF00; " +
                "-fx-border-color: #00FF00; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 20;"
        val hover = "-fx-background-color: #007700; -fx-text-fill: #00FF00; " +
                "-fx-border-color: #00FF00; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 20;"
        return componentesUI.crearBotonConHover("Fácil", normal, hover, 14.0, FontWeight.BOLD, 110.0) {
            stage.scene = PantallaMRUV(stage, menuScene).crearEscena()
            stage.title = "MRUV"
        }
    }

    /**
     * Crea el botón para iniciar el modo Difícil de MRUV.
     */
    private fun crearBotonDificil(): Button {
        val normal = "-fx-background-color: #660000; -fx-text-fill: #FF4444; " +
                "-fx-border-color: #FF4444; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 20;"
        val hover = "-fx-background-color: #880000; -fx-text-fill: #FF4444; " +
                "-fx-border-color: #FF4444; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 20;"
        return componentesUI.crearBotonConHover("Difícil", normal, hover, 14.0, FontWeight.BOLD, 110.0) {
            stage.scene = PantallaModoDificil(stage, menuScene).crearEscena()
            stage.title = "MRUV - Modo Difícil"
        }
    }
}
