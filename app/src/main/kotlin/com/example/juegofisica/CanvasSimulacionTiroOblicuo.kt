package com.example.juegofisica

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.util.Duration
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Controlador del lienzo y bucle de animación para la simulación de Tiro Oblicuo (SRP).
 *
 * Delega los cálculos matemáticos en [CalculadorTiroOblicuo] y el trazado de los ejes,
 * la grilla de 10m en 10m y la pelota en [RenderizadorPlanoCartesiano].
 *
 * @property calculador motor físico encargado de evaluar la trayectoria y el paso temporal.
 * @property onActualizarEstado callback opcional para informar (x, y, yMax, t) durante la simulación.
 */
class CanvasSimulacionTiroOblicuo(
    private val calculador: CalculadorTiroOblicuo = CalculadorTiroOblicuo(),
    private val onActualizarEstado: ((x: Double, y: Double, yMax: Double, t: Double) -> Unit)? = null
) {

    /** Canvas de JavaFX donde se dibuja el plano cartesiano y la trayectoria. */
    val canvas: Canvas = Canvas(400.0, 300.0)

    /** Renderizador dedicado para los ejes cartesianos y la pelota blanca (SRP). */
    private val renderizadorPlano: RenderizadorPlanoCartesiano = RenderizadorPlanoCartesiano()

    /** Timeline para el bucle de simulación a 40 FPS. */
    private var timeline: Timeline? = null

    /** Velocidad inicial v₀ activa en la simulación (m/s). */
    private var v0Actual: Double = 30.0

    /** Aceleración de la gravedad g activa en la simulación (m/s²). */
    private var gravedadActual: Double = 9.8

    /** Alcance horizontal total de la parábola actual (m). */
    private var alcanceTotalX: Double = 100.0

    /** Altura máxima (pico) de la parábola actual (m). */
    private var alturaPicoY: Double = 20.0

    /** Ángulo de lanzamiento θ activo en la simulación (grados). */
    private var anguloActual: Double = 45.0

    /** Tiempo físico transcurrido de la simulación (s). */
    private var tiempoActual: Double = 0.0

    /** Tiempo total de vuelo derivado de v₀ hasta que la pelota vuelve a tocar el suelo (y = 0). */
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

    /** Límite visible del eje X en metros (múltiplo exacto de 10m). */
    private var maxEscalaX: Double = 100.0

    /** Límite visible del eje Y en metros (múltiplo exacto de 10m). */
    private var maxEscalaY: Double = 60.0

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
     * Configura los parámetros e inicia la simulación animada desde el origen (0, 0) con velocidad
     * visual directamente proporcional a v₀.
     */
    fun simular(v0: Double, gravedad: Double, xf: Double, yf: Double, anguloGrados: Double) {
        detener()
        configurarParametrosFisicos(v0, gravedad, max(0.0, xf), max(0.0, yf), anguloGrados)
        reiniciarEstadoEnOrigen()
        ajustarEscalaZoom10m()
        onActualizarEstado?.invoke(pelotaX, pelotaY, alturaMaximaRecorrida, tiempoActual)
        renderizar()

        if (tiempoVueloTotal <= 0.0) return

        val duracionVisualSegundos = calculador.calcularDuracionVisualSegundos(v0Actual)
        val pasoTiempoFisico = tiempoVueloTotal / max(6.0, duracionVisualSegundos * 40.0)

        timeline = Timeline(KeyFrame(Duration.millis(25.0), { avanzarPaso(pasoTiempoFisico) })).apply {
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
        maxEscalaX = 100.0
        maxEscalaY = 60.0
        reiniciarEstadoEnOrigen()
        estelaX.clear()
        estelaY.clear()
        onActualizarEstado?.invoke(0.0, 0.0, 0.0, 0.0)
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
        val gc = canvas.graphicsContext2D
        val w = canvas.width
        val h = canvas.height
        if (w <= 0.0 || h <= 0.0) return

        gc.fill = Color.BLACK
        gc.fillRect(0.0, 0.0, w, h)

        val origenX = 50.0
        val origenY = h - 42.0
        val anchoUtil = max(50.0, w - origenX - 24.0)
        val altoUtil = max(50.0, origenY - 24.0)
        val pixelesPorMetroX = anchoUtil / max(10.0, maxEscalaX)
        val pixelesPorMetroY = altoUtil / max(10.0, maxEscalaY)

        renderizadorPlano.dibujarEjesYGrilla10m(
            gc, origenX, origenY, anchoUtil, altoUtil,
            maxEscalaX, maxEscalaY, pixelesPorMetroX, pixelesPorMetroY
        )
        renderizadorPlano.dibujarTrayectoriaYPelota(
            gc, origenX, origenY, pixelesPorMetroX, pixelesPorMetroY,
            estelaX, estelaY, pelotaX, pelotaY
        )
    }

    private fun configurarParametrosFisicos(
        v0: Double,
        gravedad: Double,
        xfDeseada: Double,
        yfDeseada: Double,
        anguloGrados: Double
    ) {
        gravedadActual = if (gravedad > 0.0) gravedad else 9.8
        anguloActual = if (xfDeseada > 0.0 && yfDeseada > 0.0 && v0 <= 0.0) {
            calculador.calcularAnguloParaPicoYAlcance(xfDeseada, yfDeseada, anguloGrados)
        } else {
            anguloGrados.coerceIn(5.0, 85.0)
        }
        v0Actual = calculador.calcularVelocidadInicialRequerida(v0, xfDeseada, yfDeseada, anguloActual, gravedadActual)
        alcanceTotalX = if (xfDeseada > 0.0) {
            xfDeseada
        } else {
            max(1.0, calculador.calcularAlcanceHorizontal(0.0, v0Actual, anguloActual, gravedadActual))
        }
        alturaPicoY = if (yfDeseada > 0.0) {
            yfDeseada
        } else {
            max(1.0, calculador.calcularAlturaMaxima(0.0, v0Actual, anguloActual, gravedadActual))
        }
        tiempoVueloTotal = calculador.calcularTiempoVueloConVelocidad(v0Actual, alcanceTotalX, anguloActual, gravedadActual)
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
        val mitadVuelo = tiempoVueloTotal * 0.5
        tiempoActual = if (tiempoActual < mitadVuelo && tiempoActual + dt > mitadVuelo) {
            mitadVuelo
        } else {
            min(tiempoActual + dt, tiempoVueloTotal)
        }

        val progreso = if (tiempoVueloTotal > 0.0) (tiempoActual / tiempoVueloTotal).coerceIn(0.0, 1.0) else 1.0
        if (tiempoActual >= tiempoVueloTotal || progreso >= 1.0) {
            tiempoActual = tiempoVueloTotal
            pelotaX = alcanceTotalX
            pelotaY = 0.0
            alturaMaximaRecorrida = max(alturaMaximaRecorrida, alturaPicoY)
            detener()
        } else {
            pelotaX = alcanceTotalX * progreso
            pelotaY = calculador.calcularYParabolaCompleta(alturaPicoY, progreso)
            alturaMaximaRecorrida = max(alturaMaximaRecorrida, pelotaY)
        }

        estelaX.add(pelotaX)
        estelaY.add(pelotaY)
        onActualizarEstado?.invoke(pelotaX, pelotaY, alturaMaximaRecorrida, tiempoActual)
        renderizar()
    }

    private fun ajustarEscalaZoom10m() {
        maxEscalaX = max(50.0, ceil((max(10.0, alcanceTotalX) * 1.15) / 10.0) * 10.0)
        maxEscalaY = max(40.0, ceil((max(10.0, alturaPicoY) * 1.20) / 10.0) * 10.0)
    }
}
