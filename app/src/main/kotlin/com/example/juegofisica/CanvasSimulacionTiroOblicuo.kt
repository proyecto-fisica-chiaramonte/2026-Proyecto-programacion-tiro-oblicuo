package com.example.juegofisica

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import javafx.util.Duration
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Controlador del lienzo y bucle de animación para la simulación de Tiro Oblicuo (SRP).
 *
 * Delega los cálculos matemáticos en [CalculadorTiroOblicuo] y el trazado de los ejes,
 * la grilla de 10m en 10m y la pelota en [RenderizadorPlanoCartesiano].
 */
class CanvasSimulacionTiroOblicuo(
    private val calculador: CalculadorTiroOblicuo = CalculadorTiroOblicuo(),
    private val onActualizarEstado: ((String) -> Unit)? = null
) {

    /** Canvas de JavaFX donde se dibuja el plano cartesiano y la trayectoria. */
    private val canvas: Canvas = Canvas(400.0, 300.0)

    /** Renderizador dedicado para los ejes cartesianos y la pelota blanca (SRP). */
    private val renderizadorPlano: RenderizadorPlanoCartesiano = RenderizadorPlanoCartesiano()

    /** Timeline para el bucle de simulación a 40 FPS. */
    private var timeline: Timeline? = null

    /** Alcance horizontal total de la parábola actual (m). */
    private var alcanceTotalX: Double = 100.0

    /** Altura máxima (pico) de la parábola actual (m). */
    private var alturaPicoY: Double = 20.0

    /** Tiempo físico transcurrido de la simulación (s). */
    private var tiempoActual: Double = 0.0

    /** Tiempo total de vuelo derivado de v₀ hasta volver a tocar el suelo (y = 0). */
    private var tiempoVueloTotal: Double = 0.0

    /** Coordenada X actual de la pelota blanca en metros. */
    private var pelotaX: Double = 0.0

    /** Coordenada Y actual de la pelota blanca en metros. */
    private var pelotaY: Double = 0.0

    /** Altura máxima alcanzada hasta el instante actual de la simulación (m). */
    private var alturaMaximaRecorrida: Double = 0.0

    /** Historial de coordenadas X (en metros) para trazar la estela blanca. */
    private val estelaX = mutableListOf<Double>()

    /** Historial de coordenadas Y (en metros) para trazar la estela blanca. */
    private val estelaY = mutableListOf<Double>()

    /**
     * Devuelve el nodo Canvas para integrarlo en el contenedor visual.
     */
    fun obtenerCanvas(): Canvas = canvas

    /**
     * Vincula el tamaño del Canvas al contenedor y redibuja automáticamente al cambiar dimensiones.
     */
    fun vincularTamano(contenedor: Pane) {
        canvas.widthProperty().bind(contenedor.widthProperty())
        canvas.heightProperty().bind(contenedor.heightProperty())
        canvas.widthProperty().addListener { _, _, _ -> renderizar() }
        canvas.heightProperty().addListener { _, _, _ -> renderizar() }
    }

    /**
     * Inicia la simulación animada a partir del objeto de dominio [ParametrosTiroOblicuo].
     */
    fun simular(parametros: ParametrosTiroOblicuo) {
        detener()
        val v0 = configurarTrayectoria(parametros)
        reiniciarEstadoEnOrigen()
        notificarEstado()
        renderizar()
        if (tiempoVueloTotal <= 0.0) return

        val cuadros = max(6.0, calculador.calcularDuracionVisualSegundos(v0) * 40.0)
        val dt = tiempoVueloTotal / cuadros
        timeline = Timeline(KeyFrame(Duration.millis(25.0), { avanzarPaso(dt) })).apply {
            cycleCount = Timeline.INDEFINITE
            play()
        }
    }

    /**
     * Detiene la simulación, borra la estela y devuelve la pelota al origen (0, 0).
     */
    fun restablecer() {
        detener()
        tiempoVueloTotal = 0.0
        alcanceTotalX = 0.0
        alturaPicoY = 0.0
        reiniciarEstadoEnOrigen()
        estelaX.clear()
        estelaY.clear()
        notificarEstado()
        renderizar()
    }

    /**
     * Detiene el Timeline activo si está en ejecución.
     */
    fun detener() {
        timeline?.stop()
        timeline = null
    }

    /**
     * Dibuja el plano cartesiano negro, los ejes graduados de 10m en 10m y la pelota blanca.
     */
    fun renderizar() {
        if (canvas.width <= 0.0 || canvas.height <= 0.0) return
        val maxX = max(50.0, ceil((max(10.0, alcanceTotalX) * 1.15) / 10.0) * 10.0)
        val maxY = max(40.0, ceil((max(10.0, alturaPicoY) * 1.20) / 10.0) * 10.0)
        val gc = canvas.graphicsContext2D
        renderizadorPlano.configurarVista(canvas, maxX, maxY)
        renderizadorPlano.dibujarPlanoBase(gc, canvas.width, canvas.height)
        renderizadorPlano.dibujarEstela(gc, estelaX, estelaY)
        renderizadorPlano.dibujarPelota(gc, pelotaX, pelotaY)
    }

    private fun configurarTrayectoria(parametros: ParametrosTiroOblicuo): Double {
        calculador.configurarGravedad(parametros.obtenerGravedad())
        val angulo = if (parametros.requiereAnguloAutomatico()) {
            calculador.calcularAnguloParaPicoYAlcance(parametros.obtenerXf(), parametros.obtenerYf(), parametros.obtenerAnguloGrados())
        } else {
            parametros.obtenerAnguloGrados()
        }
        val v0 = calculador.calcularVelocidadInicialRequerida(parametros)
        alcanceTotalX = if (parametros.obtenerXf() > 0.0) parametros.obtenerXf() else max(1.0, calculador.calcularAlcanceHorizontal(v0, angulo))
        alturaPicoY = if (parametros.obtenerYf() > 0.0) parametros.obtenerYf() else max(1.0, calculador.calcularAlturaMaxima(v0, angulo))
        tiempoVueloTotal = calculador.calcularTiempoVueloConVelocidad(v0, alcanceTotalX, angulo)
        return v0
    }

    private fun reiniciarEstadoEnOrigen() {
        tiempoActual = 0.0
        pelotaX = 0.0
        pelotaY = 0.0
        alturaMaximaRecorrida = 0.0
        estelaX.clear()
        estelaY.clear()
        estelaX.add(0.0)
        estelaY.add(0.0)
    }

    private fun avanzarPaso(dt: Double) {
        val mitad = tiempoVueloTotal * 0.5
        tiempoActual = if (tiempoActual < mitad && tiempoActual + dt > mitad) mitad else min(tiempoActual + dt, tiempoVueloTotal)
        val progreso = if (tiempoVueloTotal > 0.0) (tiempoActual / tiempoVueloTotal).coerceIn(0.0, 1.0) else 1.0
        pelotaX = if (progreso >= 1.0) alcanceTotalX else alcanceTotalX * progreso
        pelotaY = if (progreso >= 1.0) 0.0 else calculador.calcularYParabolaCompleta(alturaPicoY, progreso)
        alturaMaximaRecorrida = max(alturaMaximaRecorrida, if (progreso >= 1.0) alturaPicoY else pelotaY)
        if (progreso >= 1.0) detener()
        estelaX.add(pelotaX)
        estelaY.add(pelotaY)
        notificarEstado()
        renderizar()
    }

    private fun notificarEstado() {
        val texto = String.format(
            java.util.Locale.forLanguageTag("es-AR"),
            "x = %.1f m | y = %.1f m | y_máx = %.1f m | t = %.2f s",
            pelotaX, pelotaY, alturaMaximaRecorrida, tiempoActual
        )
        onActualizarEstado?.invoke(texto)
    }
}
