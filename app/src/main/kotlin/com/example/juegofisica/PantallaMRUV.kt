package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text

/**
 * Pantalla que se muestra al seleccionar "MRUV" desde el menú principal.
 * Muestra un fondo negro con un título y un botón para volver al menú.
 *
 * @param onVolver función que se ejecuta al presionar "Volver atrás",
 *                 restaura la escena del menú principal en el Stage
 */
class PantallaMRUV(private val onVolver: () -> Unit) {

    /**
     * Crea y devuelve la Scene correspondiente a la pantalla de MRUV.
     * Incluye un botón "Volver atrás" que navega al menú principal.
     */
    fun crearEscena(): Scene {
        // Botón para volver al menú principal
        val btnVolver = Button("Volver atrás").apply {
            font = Font.font("System", FontWeight.NORMAL, 14.0)
            style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                    "-fx-border-color: #666666; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 8 16;"
            onMouseEntered = {
                style = "-fx-background-color: #444444; -fx-text-fill: white; " +
                        "-fx-border-color: #888888; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8 16;"
            }
            onMouseExited = {
                style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                        "-fx-border-color: #666666; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8 16;"
            }
            setOnAction { onVolver() }
        }

        // Contenedor principal con fondo negro
        val root = StackPane().apply {
            style = "-fx-background-color: black;"
            padding = Insets(20.0)
        }

        // Título de la pantalla centrado
        val titulo = Text("MRUV").apply {
            font = Font.font("System", FontWeight.BOLD, 36.0)
            fill = Color.WHITE
        }

        // Posicionar el botón en la esquina inferior izquierda
        BorderPane().apply {
            center = titulo
            bottom = btnVolver
            BorderPane.setAlignment(btnVolver, Pos.BOTTOM_LEFT)
            BorderPane.setMargin(btnVolver, Insets(10.0))
            StackPane.setAlignment(this, Pos.CENTER)
            root.children.add(this)
        }

        return Scene(root, 400.0, 300.0)
    }
}
