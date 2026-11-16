package com.example.juegofisica

import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage

/**
 * Pantalla de victoria correspondiente al nivel MRUV que extiende [PantallaResultadoBase]
 * cumpliendo con el Principio Abierto/Cerrado (OCP) y Sustitución de Liskov (LSP).
 */
class PantallaVictoriaMRUV(
    private val stage: Stage,
    private val menuScene: Scene,
    private val tiempoEmpleadoSegundos: Int = 0
) : PantallaResultadoBase(
    tituloTexto = "¡Ganaste!",
    colorAcentoHex = "#00FF00",
    subtituloTexto = String.format(
        "Tiempo: %02d:%02d",
        tiempoEmpleadoSegundos.coerceAtLeast(0) / 60,
        tiempoEmpleadoSegundos.coerceAtLeast(0) % 60
    )
), PantallaJuego {

    /** Indica si el fondo animado corresponde al modo difícil (true) o fácil (false). */
    private var modoDificil: Boolean = true

    /**
     * Configura el modo de dificultad para el escenario animado de fondo y devuelve esta instancia.
     */
    fun configurarModo(esDificil: Boolean): PantallaVictoriaMRUV {
        modoDificil = esDificil
        return this
    }

    /**
     * Devuelve los segundos empleados por el jugador para resolver el problema.
     */
    fun obtenerTiempoEmpleadoSegundos(): Int = tiempoEmpleadoSegundos

    /**
     * Construye y devuelve la Scene de victoria reutilizando la tarjeta de [PantallaResultadoBase].
     */
    override fun crearEscena(): Scene {
        val root = StackPane()
        val animacionFondo = AnimacionEscenario(modoDificil).apply {
            vincularTamano(root)
            ejecutarAnimacionDesvio {}
        }
        val panelVictoria = construirTarjetaResultado("#004400") {
            animacionFondo.detener()
            stage.scene = menuScene
            stage.title = "Juego Educativo de Física"
        }
        root.children.addAll(animacionFondo.obtenerCanvas(), panelVictoria)
        StackPane.setAlignment(panelVictoria, Pos.CENTER)
        return Scene(root, 400.0, 300.0)
    }
}
