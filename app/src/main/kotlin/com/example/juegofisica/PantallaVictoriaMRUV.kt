package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage

/**
 * Pantalla de victoria correspondiente al modo difícil del nivel MRUV.
 *
 * Se muestra cuando el jugador ingresa el resultado correcto de la posición final x(t)
 * antes de que se agote el tiempo. Utiliza la imagen de fondo `fondo_mruv_d_f.jpg`,
 * muestra un mensaje festivo "¡Ganaste!" y un botón "Reiniciar" para volver al menú principal.
 *
 * @property stage el Stage principal de la aplicación.
 * @property menuScene la Scene del menú principal a la que se regresa.
 */
class PantallaVictoriaMRUV(
    private val stage: Stage,
    private val menuScene: Scene
) {

    /**
     * Construye y devuelve la Scene de victoria con la imagen de fondo fondo_mruv_d_f.jpg,
     * el mensaje festivo "¡Ganaste!" y el botón para reiniciar al menú principal.
     *
     * @return la Scene de victoria lista para asignarse al Stage principal.
     */
    fun crearEscena(): Scene {
        val root = StackPane()

        // Cargar imagen de fondo festiva del modo difícil
        val imagenFondo = Image(javaClass.getResource("/images/fondo_mruv_d_f.jpg").toExternalForm())
        val imageView = ImageView(imagenFondo).apply {
            isPreserveRatio = false
            fitWidthProperty().bind(root.widthProperty())
            fitHeightProperty().bind(root.heightProperty())
        }

        // Panel centrado festivo con bordes verdes y fondo oscuro translúcido
        val panelVictoria = VBox(20.0).apply {
            alignment = Pos.CENTER
            padding = Insets(30.0, 40.0, 30.0, 40.0)
            maxWidth = 320.0
            maxHeight = 180.0
            style = "-fx-background-color: rgba(0, 0, 0, 0.85); " +
                    "-fx-border-color: #00FF00; " +
                    "-fx-border-width: 3; " +
                    "-fx-border-radius: 12; " +
                    "-fx-background-radius: 12;"
        }

        // Mensaje destacado de victoria
        val lblGanaste = Text("¡Ganaste!").apply {
            font = Font.font("System", FontWeight.BOLD, 42.0)
            fill = Color.web("#00FF00")
        }

        // Botón Reiniciar para volver al menú principal
        val btnReiniciar = Button("Reiniciar").apply {
            font = Font.font("System", FontWeight.BOLD, 16.0)
            style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                    "-fx-border-color: #00FF00; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 10 28;"
            onMouseEntered = {
                style = "-fx-background-color: #004400; -fx-text-fill: #AAFFAA; " +
                        "-fx-border-color: #66FF66; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 10 28;"
            }
            onMouseExited = {
                style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                        "-fx-border-color: #00FF00; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 10 28;"
            }
            setOnAction {
                stage.scene = menuScene
                stage.title = "Juego Educativo de Física"
            }
        }

        panelVictoria.children.addAll(lblGanaste, btnReiniciar)
        root.children.addAll(imageView, panelVictoria)
        StackPane.setAlignment(panelVictoria, Pos.CENTER)

        return Scene(root, 400.0, 300.0)
    }
}
