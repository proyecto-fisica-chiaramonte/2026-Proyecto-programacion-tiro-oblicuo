package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Stage

/**
 * Pantalla principal del módulo interactivo de Simulación de Tiro Oblicuo (SRP).
 *
 * Coordina únicamente la disposición del lienzo [CanvasSimulacionTiroOblicuo], el panel lateral
 * [PanelControlesTiroOblicuo] y la barra inferior de telemetría, manteniendo alta cohesión.
 */
class PantallaSimulacionTiroOblicuo(
    private val stage: Stage,
    private val menuScene: Scene
) : PantallaJuego {

    /** Etiqueta de telemetría en vivo que muestra x(t), y(t), altura máxima y tiempo t. */
    private val lblEstado: Label = Label("x = 0,0 m | y = 0,0 m | y_máx = 0,0 m | t = 0,00 s").apply {
        font = Font.font("System", FontWeight.BOLD, 10.5)
        textFill = Color.web("#FFD700")
    }

    /** Lienzo dedicado para renderizar los ejes cartesianos de 10m en 10m y la animación parabólica. */
    private val canvasSimulacion: CanvasSimulacionTiroOblicuo = CanvasSimulacionTiroOblicuo(
        onActualizarEstado = { textoEstado -> lblEstado.text = textoEstado }
    )

    /** Panel lateral desacoplado con las 5 entradas y los botones de acción (SRP). */
    private val panelControles: PanelControlesTiroOblicuo = PanelControlesTiroOblicuo(
        onSimular = { parametros -> canvasSimulacion.simular(parametros) },
        onVolverMenu = { volverAlMenuPrincipal() }
    )

    /**
     * Construye y devuelve la Scene completa de la simulación de Tiro Oblicuo.
     */
    override fun crearEscena(): Scene {
        panelControles.vincularAccionRestablecer { canvasSimulacion.restablecer() }
        val panelCanvas = Pane(canvasSimulacion.obtenerCanvas()).apply {
            style = "-fx-background-color: #000000;"
        }
        canvasSimulacion.vincularTamano(panelCanvas)

        val root = BorderPane().apply {
            style = "-fx-background-color: #000000;"
            left = panelControles.construirPanel()
            center = panelCanvas
            bottom = construirBarraEstado()
        }

        canvasSimulacion.restablecer()
        return Scene(root, 560.0, 350.0, Color.BLACK)
    }

    /**
     * Restablece los parámetros del panel lateral y el lienzo a sus valores por defecto.
     */
    fun restablecerParametros() {
        panelControles.restablecerParametros()
    }

    private fun construirBarraEstado(): HBox {
        return HBox(lblEstado).apply {
            alignment = Pos.CENTER
            padding = Insets(4.0, 8.0, 4.0, 8.0)
            style = "-fx-background-color: #0A0A0A; -fx-border-color: #222222; -fx-border-width: 1 0 0 0;"
        }
    }

    private fun volverAlMenuPrincipal() {
        canvasSimulacion.detener()
        stage.scene = menuScene
        stage.title = "Juego Educativo de Física"
    }
}
