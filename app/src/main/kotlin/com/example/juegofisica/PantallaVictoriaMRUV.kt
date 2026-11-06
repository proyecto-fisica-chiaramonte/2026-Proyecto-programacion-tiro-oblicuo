package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage

/**
 * Pantalla de victoria correspondiente al nivel MRUV (SRP / DRY).
 *
 * Presenta el escenario animado de desvío como fondo, el mensaje "¡Ganaste!", el tiempo
 * transcurrido formateado y el botón "Reiniciar" construido mediante [ComponentesUI].
 */
class PantallaVictoriaMRUV(
    private val stage: Stage,
    private val menuScene: Scene,
    val tiempoEmpleadoSegundos: Int = 0,
    private val esModoDificil: Boolean = true
) : PantallaJuego {

    /** Proveedor compartido de componentes de UI (DRY). */
    private val componentesUI: ComponentesUI = ComponentesUI()

    /**
     * Construye y devuelve la Scene de victoria.
     */
    override fun crearEscena(): Scene {
        val root = StackPane()

        val animacionFondo = AnimacionEscenario(esModoDificil).apply {
            vincularTamano(root)
            ejecutarAnimacionDesvio {}
        }

        val panelVictoria = construirPanelCentral(animacionFondo)
        root.children.addAll(animacionFondo.canvas, panelVictoria)
        StackPane.setAlignment(panelVictoria, Pos.CENTER)

        return Scene(root, 400.0, 300.0)
    }

    /**
     * Construye la tarjeta central de victoria con el tiempo empleado y el botón de reinicio.
     */
    private fun construirPanelCentral(animacionFondo: AnimacionEscenario): VBox {
        val lblGanaste = Text("¡Ganaste!").apply {
            font = Font.font("System", FontWeight.BOLD, 38.0)
            fill = Color.web("#00FF00")
        }

        val totalSegundos = tiempoEmpleadoSegundos.coerceAtLeast(0)
        val tiempoTexto = String.format("%02d:%02d", totalSegundos / 60, totalSegundos % 60)
        val lblTiempo = Text("Tiempo: $tiempoTexto").apply {
            font = Font.font("System", FontWeight.BOLD, 20.0)
            fill = Color.WHITE
        }

        val btnReiniciar = componentesUI.crearBotonConHover(
            texto = "Reiniciar",
            estiloNormal = "-fx-background-color: #333333; -fx-text-fill: white; " +
                    "-fx-border-color: #00FF00; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 26;",
            estiloHover = "-fx-background-color: #004400; -fx-text-fill: #AAFFAA; " +
                    "-fx-border-color: #66FF66; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 26;",
            tamanoFuente = 15.0
        ) {
            animacionFondo.detener()
            stage.scene = menuScene
            stage.title = "Juego Educativo de Física"
        }

        return VBox(15.0, lblGanaste, lblTiempo, btnReiniciar).apply {
            alignment = Pos.CENTER
            padding = Insets(25.0, 35.0, 25.0, 35.0)
            maxWidth = 320.0
            maxHeight = 200.0
            style = "-fx-background-color: rgba(0, 0, 0, 0.85); -fx-border-color: #00FF00; " +
                    "-fx-border-width: 3; -fx-border-radius: 12; -fx-background-radius: 12;"
        }
    }
}
