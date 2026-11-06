package com.example.juegofisica

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.util.Duration
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Lienzo interactivo para renderizar los ejes cartesianos graduados de 10m en 10m y la simulación
 * física completa del Tiro Oblicuo partiendo desde el origen (0, 0) (SRP).
 *
 * Dibuja un fondo completamente negro (`Color.BLACK`), el eje horizontal X en color azul (`Color.BLUE`)
 * y el eje vertical Y en color rojo (`Color.RED`) con marcas y etiquetas cada 10 metros
 * (10m, 20m, 30m, 40m, ...), calculando dinámicamente la conversión metros-a-píxeles para que la
 * parábola completa (subiendo hasta la altura máxima y_f y cayendo a tierra en x_f) quepa en el Canvas.
 *
 * @property calculador motor físico encargado de evaluar x(t), y(t), v₀ y θ.
 * @property onActualizarEstado callback opcional para informar (x, y, yMax, t) durante la simulación.
 */
class CanvasSimulacionTiroOblicuo(
    private val calculador: CalculadorTiroOblicuo = CalculadorTiroOblicuo(),
    private val onActualizarEstado: ((x: Double, y: Double, yMax: Double, t: Double) -> Unit)? = null
) {

    /** Canvas de JavaFX donde se dibuja el plano cartesiano y la trayectoria. */
    val canvas: Canvas = Canvas(400.0, 300.0)

    /** Timeline para el bucle de simulación a 40 FPS. */
    private var timeline: Timeline? = null

    /** Velocidad inicial v₀ activa en la simulación (m/s). */
    private var v0Actual: Double = 30.0

    /** Aceleración de la gravedad g activa en la simulación (m/s²). */
    private var gravedadActual: Double = 9.8

    /** Alcance horizontal objetivo X (x_f) ingresado por el usuario (m). */
    private var xfDeseada: Double = 0.0

    /** Altura máxima/objetivo Y (y_f) ingresada por el usuario (m). */
    private var yfDeseada: Double = 0.0

    /** Ángulo de lanzamiento θ activo en la simulación (grados). */
    private var anguloActual: Double = 45.0

    /** Tiempo físico transcurrido de la simulación (s). */
    private var tiempoActual: Double = 0.0

    /** Tiempo total de vuelo hasta que la pelota vuelve a tocar el suelo (y = 0). */
    private var tiempoVueloTotal: Double = 0.0

    /** Coordenada X actual de la pelota blanca en metros. */
    private var pelotaX: Double = 0.0

    /** Coordenada Y actual de la pelota blanca en metros. */
    private var pelotaY: Double = 0.0

    /** Altura máxima alcanzada hasta el instante actual de la simulación (m). */
    private var alturaMaximaRecorrida: Double = 0.0

    /** Historial de coordenadas X (en metros) para trazar la estela blanca de la trayectoria. */
    private val estelaX = mutableListOf<Double>()

    /** Historial de coordenadas Y (en metros) para trazar la estela blanca de la trayectoria. */
    private val estelaY = mutableListOf<Double>()

    /** Límite visible del eje X en metros (múltiplo exacto de 10m). */
    private var maxEscalaX: Double = 100.0

    /** Límite visible del eje Y en metros (múltiplo exacto de 10m). */
    private var maxEscalaY: Double = 60.0

    /** Factor de conversión horizontal en píxeles por metro (Auto-Zoom). */
    private var pixelesPorMetroX: Double = 1.0

    /** Factor de conversión vertical en píxeles por metro (Auto-Zoom). */
    private var pixelesPorMetroY: Double = 1.0

    /**
     * Vincula el tamaño del Canvas al contenedor y redibuja automáticamente al cambiar dimensiones.
     *
     * @param contenedor panel contenedor del Canvas.
     */
    fun vincularTamano(contenedor: Pane) {
        canvas.widthProperty().bind(contenedor.widthProperty())
        canvas.heightProperty().bind(contenedor.heightProperty())
        canvas.widthProperty().addListener { _, _, _ -> renderizar() }
        canvas.heightProperty().addListener { _, _, _ -> renderizar() }
    }

    /**
     * Configura los parámetros e inicia la simulación animada desde el origen (0, 0) durante todo
     * el tiempo de vuelo t_total hasta que la pelota vuelve a tocar el suelo (y = 0) en x_f.
     *
     * Si el usuario ingresa x_f > 0 e y_f > 0 (ej. x_f = 10, y_f = 10), calcula automáticamente
     * v₀ y θ para que el proyectil suba pasando exactamente por el pico y = y_f y descienda hasta
     * impactar en el suelo (y = 0) en x = x_f.
     */
    fun simular(v0: Double, gravedad: Double, xf: Double, yf: Double, anguloGrados: Double) {
        detener()
        gravedadActual = if (gravedad > 0.0) gravedad else 9.8
        xfDeseada = max(0.0, xf)
        yfDeseada = max(0.0, yf)

        if (xfDeseada > 0.0 && yfDeseada > 0.0) {
            anguloActual = calculador.calcularAnguloParaPicoYAlcance(xfDeseada, yfDeseada, anguloGrados)
            v0Actual = calculador.calcularVelocidadInicialRequerida(
                v0,
                xfDeseada,
                yfDeseada,
                anguloActual,
                gravedadActual
            )
        } else if (xfDeseada > 0.0 || yfDeseada > 0.0 || v0 <= 0.0) {
            anguloActual = anguloGrados.coerceIn(5.0, 85.0)
            v0Actual = calculador.calcularVelocidadInicialRequerida(
                v0,
                xfDeseada,
                yfDeseada,
                anguloActual,
                gravedadActual
            )
        } else {
            anguloActual = anguloGrados.coerceIn(5.0, 85.0)
            v0Actual = v0
        }

        tiempoVueloTotal = calculador.calcularTiempoVuelo(0.0, v0Actual, anguloActual, gravedadActual)

        // El tiro comienza siempre desde el origen cartesiano (0, 0)
        tiempoActual = 0.0
        pelotaX = 0.0
        pelotaY = 0.0
        alturaMaximaRecorrida = 0.0

        estelaX.clear()
        estelaY.clear()
        estelaX.add(pelotaX)
        estelaY.add(pelotaY)

        ajustarEscalaZoom10m()
        onActualizarEstado?.invoke(pelotaX, pelotaY, alturaMaximaRecorrida, tiempoActual)
        renderizar()

        if (tiempoVueloTotal <= 0.0) {
            return
        }

        // Duración visual fluida (~2.8 s en pantalla) asegurando pasar por el vértice t_total / 2
        val pasosTotales = 110
        val pasoTiempoFisico = tiempoVueloTotal / pasosTotales.toDouble()

        timeline = Timeline(
            KeyFrame(Duration.millis(25.0), {
                avanzarPaso(pasoTiempoFisico)
            })
        ).apply {
            cycleCount = Timeline.INDEFINITE
            play()
        }
    }

    /**
     * Detiene la simulación, borra la estela, devuelve la pelota al origen (0, 0)
     * y restaura la grilla base de 10m en 10m.
     */
    fun restablecer() {
        detener()
        tiempoActual = 0.0
        tiempoVueloTotal = 0.0
        pelotaX = 0.0
        pelotaY = 0.0
        alturaMaximaRecorrida = 0.0
        xfDeseada = 0.0
        yfDeseada = 0.0
        estelaX.clear()
        estelaY.clear()

        maxEscalaX = 100.0
        maxEscalaY = 60.0

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
     * Evalúa x(t) = v₀·cos(θ)·t e y(t) = v₀·sen(θ)·t - ½·g·t² durante todo el tiempo de vuelo
     * t_total hasta que la pelota vuelve a tocar el suelo (y = 0) en x_f.
     */
    private fun avanzarPaso(dt: Double) {
        val mitadVuelo = tiempoVueloTotal * 0.5
        val siguienteTiempo = if (tiempoActual < mitadVuelo && tiempoActual + dt > mitadVuelo) {
            mitadVuelo
        } else {
            min(tiempoActual + dt, tiempoVueloTotal)
        }
        tiempoActual = siguienteTiempo

        val xCalc = calculador.calcularX(0.0, v0Actual, anguloActual, tiempoActual)
        val yCalc = calculador.calcularY(0.0, v0Actual, anguloActual, gravedadActual, tiempoActual)

        if (tiempoActual >= tiempoVueloTotal || (yCalc <= 0.0 && tiempoActual > mitadVuelo)) {
            tiempoActual = tiempoVueloTotal
            pelotaX = if (xfDeseada > 0.0) xfDeseada else calculador.calcularX(0.0, v0Actual, anguloActual, tiempoVueloTotal)
            pelotaY = 0.0
            alturaMaximaRecorrida = max(
                alturaMaximaRecorrida,
                calculador.calcularAlturaMaxima(0.0, v0Actual, anguloActual, gravedadActual)
            )
            estelaX.add(pelotaX)
            estelaY.add(pelotaY)
            detener()
        } else {
            pelotaX = xCalc
            pelotaY = max(0.0, yCalc)
            alturaMaximaRecorrida = max(alturaMaximaRecorrida, pelotaY)
            estelaX.add(pelotaX)
            estelaY.add(pelotaY)
        }

        onActualizarEstado?.invoke(pelotaX, pelotaY, alturaMaximaRecorrida, tiempoActual)
        renderizar()
    }

    /**
     * Ajusta el rango máximo de los ejes en múltiplos exactos de 10 metros para que toda la
     * parábola (su pico y_f y su caída en x_f) entre dentro del área visible del Canvas.
     */
    private fun ajustarEscalaZoom10m() {
        val alcanceFisicoX = calculador.calcularAlcanceHorizontal(0.0, v0Actual, anguloActual, gravedadActual)
        val alturaMaximaFisicaY = calculador.calcularAlturaMaxima(0.0, v0Actual, anguloActual, gravedadActual)

        val maxRequeridoX = max(alcanceFisicoX, xfDeseada)
        val maxRequeridoY = max(alturaMaximaFisicaY, yfDeseada)

        // Redondear hacia arriba al siguiente múltiplo de 10m con margen visual del 15-20%
        maxEscalaX = max(50.0, ceil((maxRequeridoX * 1.15) / 10.0) * 10.0)
        maxEscalaY = max(40.0, ceil((maxRequeridoY * 1.20) / 10.0) * 10.0)
    }

    /**
     * Dibuja el fondo negro, la grilla y ejes cartesianos (X azul, Y rojo) con marcas cada 10 metros,
     * la trayectoria blanca y la pelota blanca en su posición física actual.
     */
    fun renderizar() {
        val gc = canvas.graphicsContext2D
        val w = canvas.width
        val h = canvas.height
        if (w <= 0.0 || h <= 0.0) return

        // 1. Fondo completamente negro (Color.BLACK)
        gc.fill = Color.BLACK
        gc.fillRect(0.0, 0.0, w, h)

        val margenIzq = 50.0
        val margenDer = 24.0
        val margenSup = 24.0
        val margenInf = 42.0

        val anchoUtil = max(50.0, w - margenIzq - margenDer)
        val altoUtil = max(50.0, h - margenSup - margenInf)
        val origenX = margenIzq
        val origenY = h - margenInf

        // Conversión dinámica metros-a-píxeles (Auto-Ajuste de Zoom)
        pixelesPorMetroX = anchoUtil / max(10.0, maxEscalaX)
        pixelesPorMetroY = altoUtil / max(10.0, maxEscalaY)

        // 2. Grilla y Ejes Cartesianos graduados de 10m en 10m (X azul, Y rojo)
        dibujarEjesYGrilla10m(gc, origenX, origenY, anchoUtil, altoUtil)

        // 3. Estela blanca continua/punteada a medida que la pelota avanza desde (0, 0)
        if (estelaX.size > 1) {
            gc.save()
            gc.stroke = Color.WHITE
            gc.lineWidth = 2.0
            gc.setLineDashes(5.0, 4.0)
            for (i in 0 until estelaX.size - 1) {
                val px1 = origenX + estelaX[i] * pixelesPorMetroX
                val py1 = origenY - estelaY[i] * pixelesPorMetroY
                val px2 = origenX + estelaX[i + 1] * pixelesPorMetroX
                val py2 = origenY - estelaY[i + 1] * pixelesPorMetroY
                gc.strokeLine(px1, py1, px2, py2)
            }
            gc.restore()
        }

        // 4. Pelota de color blanco (Color.WHITE) en su coordenada exacta (x(t), y(t))
        val radioPelota = 6.0
        val pelotaPantallaX = origenX + pelotaX * pixelesPorMetroX
        val pelotaPantallaY = origenY - pelotaY * pixelesPorMetroY

        gc.fill = Color.WHITE
        gc.fillOval(
            pelotaPantallaX - radioPelota,
            pelotaPantallaY - radioPelota,
            radioPelota * 2.0,
            radioPelota * 2.0
        )
    }

    /**
     * Dibuja el eje horizontal X en azul (Color.BLUE), el eje vertical Y en rojo (Color.RED),
     * la grilla de referencia y las marcas de escala incrementando exactamente de 10m en 10m
     * (10m, 20m, 30m, 40m, ...).
     */
    private fun dibujarEjesYGrilla10m(
        gc: GraphicsContext,
        origenX: Double,
        origenY: Double,
        anchoUtil: Double,
        altoUtil: Double
    ) {
        gc.font = Font.font("System", FontWeight.BOLD, 9.5)

        val limiteMetrosX = maxEscalaX.toInt()
        val limiteMetrosY = maxEscalaY.toInt()

        // Líneas de grilla cada 10 metros
        gc.stroke = Color.web("#181818")
        gc.lineWidth = 1.0
        var mGridX = 10
        while (mGridX <= limiteMetrosX) {
            val px = origenX + mGridX * pixelesPorMetroX
            gc.strokeLine(px, origenY, px, origenY - altoUtil)
            mGridX += 10
        }
        var mGridY = 10
        while (mGridY <= limiteMetrosY) {
            val py = origenY - mGridY * pixelesPorMetroY
            gc.strokeLine(origenX, py, origenX + anchoUtil, py)
            mGridY += 10
        }

        // Eje horizontal X en color azul (Color.BLUE)
        gc.stroke = Color.BLUE
        gc.lineWidth = 2.5
        gc.strokeLine(origenX, origenY, origenX + anchoUtil, origenY)

        var ultimoTextoX = -100.0
        var mX = 10
        while (mX <= limiteMetrosX) {
            val px = origenX + mX * pixelesPorMetroX
            gc.stroke = Color.BLUE
            gc.lineWidth = 1.6
            gc.strokeLine(px, origenY - 4.0, px, origenY + 4.0)

            if (px - ultimoTextoX >= 25.0) {
                gc.fill = Color.LIGHTBLUE
                gc.fillText("${mX}m", px - 11.0, origenY + 16.0)
                ultimoTextoX = px
            }
            mX += 10
        }
        gc.fill = Color.BLUE
        gc.fillText("X (m)", origenX + anchoUtil - 26.0, origenY + 31.0)

        // Eje vertical Y en color rojo (Color.RED)
        gc.stroke = Color.RED
        gc.lineWidth = 2.5
        gc.strokeLine(origenX, origenY, origenX, origenY - altoUtil)

        var ultimoTextoY = origenY + 100.0
        var mY = 10
        while (mY <= limiteMetrosY) {
            val py = origenY - mY * pixelesPorMetroY
            gc.stroke = Color.RED
            gc.lineWidth = 1.6
            gc.strokeLine(origenX - 4.0, py, origenX + 4.0, py)

            if (ultimoTextoY - py >= 13.0) {
                gc.fill = Color.SALMON
                gc.fillText("${mY}m", 4.0, py + 3.5)
                ultimoTextoY = py
            }
            mY += 10
        }
        gc.fill = Color.RED
        gc.fillText("Y (m)", 8.0, origenY - altoUtil - 6.0)
    }
}
