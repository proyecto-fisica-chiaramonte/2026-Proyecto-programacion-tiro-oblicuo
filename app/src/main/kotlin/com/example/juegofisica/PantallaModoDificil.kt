package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.stage.Stage

/**
 * Pantalla que representa el modo difícil del nivel MRUV.
 *
 * Muestra una vista con fondo negro y un botón para volver al menú principal.
 *
 * @property stage el Stage principal de la aplicación.
 * @property menuScene la Scene del menú principal a la que se regresa.
 */
class PantallaModoDificil(
    private val stage: Stage,
    private val menuScene: Scene
) {

    /**
     * Construye y devuelve la Scene de la pantalla de modo difícil.
     *
     * @return la Scene lista para asignarse al Stage principal.
     */
    fun crearEscena(): Scene {
        val root = VBox(20.0).apply {
            alignment = Pos.CENTER
            padding = Insets(40.0)
            style = "-fx-background-color: #000000;"
        }

        val titulo = Text("Modo Difícil").apply {
            font = Font.font("System", FontWeight.BOLD, 28.0)
            fill = Color.WHITE
        }

        val descripcion = Text("Nivel en desarrollo. Próximamente disponible con nuevos desafíos de MRUV.").apply {
            font = Font.font("System", FontWeight.NORMAL, 14.0)
            fill = Color.LIGHTGRAY
            textAlignment = TextAlignment.CENTER
            wrappingWidth = 300.0
        }

        val btnVolverMenu = Button("Volver al Menú").apply {
            font = Font.font("System", FontWeight.MEDIUM, 14.0)
            style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                    "-fx-border-color: #666666; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 10 20;"
            onMouseEntered = {
                style = "-fx-background-color: #444444; -fx-text-fill: white; " +
                        "-fx-border-color: #888888; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 10 20;"
            }
            onMouseExited = {
                style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                        "-fx-border-color: #666666; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 10 20;"
            }
            setOnAction {
                stage.scene = menuScene
            }
        }

        root.children.addAll(titulo, descripcion, btnVolverMenu)

        return Scene(root, 400.0, 300.0)
    }
}
