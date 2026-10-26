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
 * Pantalla de victoria correspondiente al nivel MRUV (reutilizable para modo fácil y difícil).
 *
 * Se muestra cuando el jugador ingresa el resultado correcto antes de que se agote el tiempo.
 * Utiliza la imagen de fondo `fondo_mruv_d_f.jpg`, muestra un mensaje festivo "¡Ganaste!",
 * el tiempo exacto empleado en resolver el ejercicio y un botón "Reiniciar" para volver al menú principal.
 *
 * @property stage el Stage principal de la aplicación.
 * @property menuScene la Scene del menú principal a la que se regresa.
 * @property tiempoEmpleadoSegundos cantidad de segundos que le tomó al jugador resolver el problema.
 */
class PantallaVictoriaMRUV(
    private val stage: Stage,
    private val menuScene: Scene,
    private val tiempoEmpleadoSegundos: Int = 0
) {

    /**
     * Construye y devuelve la Scene de victoria con la imagen de fondo fondo_mruv_d_f.jpg,
     * el mensaje festivo "¡Ganaste!", el tiempo empleado y el botón para reiniciar al menú principal.
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
        val panelVictoria = VBox(15.0).apply {
            alignment = Pos.CENTER
            padding = Insets(25.0, 35.0, 25.0, 35.0)
            maxWidth = 320.0
            maxHeight = 200.0
            style = "-fx-background-color: rgba(0, 0, 0, 0.85); " +
                    "-fx-border-color: #00FF00; " +
                    "-fx-border-width: 3; " +
                    "-fx-border-radius: 12; " +
                    "-fx-background-radius: 12;"
        }

        // Mensaje destacado de victoria
        val lblGanaste = Text("¡Ganaste!").apply {
            font = Font.font("System", FontWeight.BOLD, 38.0)
            fill = Color.web("#00FF00")
        }

        // Etiqueta que muestra el tiempo transcurrido exacto formateado mm:ss
        val lblTiempo = Text("Tiempo: ${formatearTiempo(tiempoEmpleadoSegundos)}").apply {
            font = Font.font("System", FontWeight.BOLD, 20.0)
            fill = Color.WHITE
        }

        // Botón Reiniciar para volver al menú principal
        val btnReiniciar = Button("Reiniciar").apply {
            font = Font.font("System", FontWeight.BOLD, 15.0)
            style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                    "-fx-border-color: #00FF00; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 8 26;"
            onMouseEntered = {
                style = "-fx-background-color: #004400; -fx-text-fill: #AAFFAA; " +
                        "-fx-border-color: #66FF66; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8 26;"
            }
            onMouseExited = {
                style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                        "-fx-border-color: #00FF00; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8 26;"
            }
            setOnAction {
                stage.scene = menuScene
                stage.title = "Juego Educativo de Física"
            }
        }

        panelVictoria.children.addAll(lblGanaste, lblTiempo, btnReiniciar)
        root.children.addAll(imageView, panelVictoria)
        StackPane.setAlignment(panelVictoria, Pos.CENTER)

        return Scene(root, 400.0, 300.0)
    }

    /**
     * Formatea una cantidad de segundos al formato de reloj mm:ss.
     */
    private fun formatearTiempo(segundos: Int): String {
        val totalSegundos = segundos.coerceAtLeast(0)
        val minutos = totalSegundos / 60
        val segs = totalSegundos % 60
        return String.format("%02d:%02d", minutos, segs)
    }
}
