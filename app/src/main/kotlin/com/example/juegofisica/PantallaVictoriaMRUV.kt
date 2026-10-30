package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage

/**
 * Pantalla de victoria correspondiente al nivel MRUV.
 *
 * Se muestra tras concluir la secuencia animada de desvío del tren cuando el jugador
 * ingresa el resultado correcto antes de que se agote el tiempo. Presenta el escenario
 * animado de fondo, el mensaje "¡Ganaste!", el tiempo exacto transcurrido y el botón
 * "Reiniciar" para regresar al menú principal.
 *
 * Implementa la interfaz [PantallaJuego] (LSP).
 *
 * @property stage el Stage principal de la aplicación.
 * @property menuScene la Scene del menú principal a la que se regresa.
 * @property tiempoEmpleadoSegundos cantidad de segundos que le tomó al jugador resolver el problema.
 */
class PantallaVictoriaMRUV(
    private val stage: Stage,
    private val menuScene: Scene,
    val tiempoEmpleadoSegundos: Int = 0,
    private val esModoDificil: Boolean = true
) : PantallaJuego {

    /**
     * Construye y devuelve la Scene de victoria con el escenario animado de desvío,
     * el mensaje "¡Ganaste!", el tiempo empleado y el botón "Reiniciar".
     *
     * @return la Scene de victoria lista para asignarse al Stage principal.
     */
    override fun crearEscena(): Scene {
        val root = StackPane()

        val animacionFondo = AnimacionEscenario(esModoDificil)
        animacionFondo.vincularTamano(root)
        animacionFondo.ejecutarAnimacionDesvio {}

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

        // Etiqueta que muestra el tiempo empleado en la partida formateado
        val totalSegundos = tiempoEmpleadoSegundos.coerceAtLeast(0)
        val minutos = totalSegundos / 60
        val segs = totalSegundos % 60
        val tiempoTexto = String.format("%02d:%02d", minutos, segs)

        val lblTiempo = Text("Tiempo: $tiempoTexto").apply {
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
                animacionFondo.detener()
                stage.scene = menuScene
                stage.title = "Juego Educativo de Física"
            }
        }

        panelVictoria.children.addAll(lblGanaste, lblTiempo, btnReiniciar)
        root.children.addAll(animacionFondo.canvas, panelVictoria)
        StackPane.setAlignment(panelVictoria, Pos.CENTER)

        return Scene(root, 400.0, 300.0)
    }
}
