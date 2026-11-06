package com.example.juegofisica

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.util.Duration
import kotlin.math.sin
import kotlin.random.Random

/**
 * Controlador del ciclo de animación continua y estados del escenario para los niveles de MRUV (SRP).
 *
 * Delega el trazado del entorno y vías en [RenderizadorEscenarioMRUV] y el trazado de la locomotora
 * y su humo en [TrenCarbonWidget], manteniendo alta cohesión y baja complejidad ciclomática.
 *
 * @property esModoDificil define si el entorno es nocturno/tormentoso (difícil) o diurno (fácil).
 */
class AnimacionEscenario(
    private val esModoDificil: Boolean = true
) {

    /** Lienzo responsivo donde se dibuja el escenario cuadro a cuadro. */
    val canvas: Canvas = Canvas(400.0, 300.0)

    /** Renderizador dedicado para el cielo, montañas, árboles, vías y jugador (SRP). */
    private val renderizadorEntorno: RenderizadorEscenarioMRUV = RenderizadorEscenarioMRUV(esModoDificil)

    /** Componente visual dedicado a la locomotora clásica de carbón y su humo (SRP). */
    private val trenCarbon: TrenCarbonWidget = TrenCarbonWidget(esModoDificil)

    /** Bucle de animación continua a 40 FPS. */
    private var timeline: Timeline? = null

    /** Estado actual: 0 = Persecución activa, 1 = Desvío (Victoria), 2 = Impacto (Derrota). */
    private var estado: Int = 0

    /** Tiempo total acumulado en segundos para animar el ciclo de carrera y el parallax. */
    private var tiempoTotal: Double = 0.0

    /** Tiempo transcurrido dentro de la secuencia final (victoria o derrota). */
    private var tiempoSecuenciaFinal: Double = 0.0

    /** Progreso del temporizador de la partida entre 0.0 (inicio) y 1.0 (tiempo agotado). */
    private var progresoTiempo: Double = 0.0

    /** Desplazamiento horizontal acumulado de la capa de montañas (parallax lento). */
    private var offsetMontanas: Double = 0.0

    /** Desplazamiento horizontal acumulado de árboles y postes (parallax medio). */
    private var offsetArboles: Double = 0.0

    /** Desplazamiento horizontal acumulado de durmientes de la vía (parallax rápido). */
    private var offsetVias: Double = 0.0

    /** Posición X normalizada del tren (relativa al ancho del lienzo). */
    private var trenProgresoX: Double = 0.06

    /** Desplazamiento vertical del tren al tomar la vía de desvío en la victoria. */
    private var trenDesvioY: Double = 0.0

    /** Ángulo de rotación del tren durante el cambio de carril. */
    private var trenAngulo: Double = 0.0

    /** Intensidad del temblor de cámara en píxeles. */
    private var temblorCamara: Double = 0.0

    /** Indica si la secuencia final ya notificó su finalización para no repetir el callback. */
    private var secuenciaCompletada: Boolean = false

    /** Acción a ejecutar al concluir la animación de victoria o derrota. */
    private var onSecuenciaCompletada: (() -> Unit)? = null

    /**
     * Vincula las dimensiones del Canvas al contenedor raíz para adaptarse a cualquier tamaño de ventana.
     */
    fun vincularTamano(root: StackPane) {
        canvas.widthProperty().bind(root.widthProperty())
        canvas.heightProperty().bind(root.heightProperty())
    }

    /**
     * Inicia el bucle continuo de animación en modo persecución activa.
     */
    fun iniciarPersecucion() {
        estado = 0
        tiempoSecuenciaFinal = 0.0
        trenDesvioY = 0.0
        trenAngulo = 0.0
        secuenciaCompletada = false
        timeline?.stop()

        val delta = 0.025
        timeline = Timeline(
            KeyFrame(Duration.millis(25.0), {
                tiempoTotal += delta
                actualizarEstado(delta)
                renderizar()
            })
        ).apply {
            cycleCount = Timeline.INDEFINITE
            play()
        }
    }

    /**
     * Actualiza el acercamiento gradual de la locomotora según el tiempo restante de la partida.
     */
    fun actualizarProgreso(progreso: Double) {
        progresoTiempo = progreso.coerceIn(0.0, 1.0)
    }

    /**
     * Activa la secuencia animada de victoria donde la locomotora toma la vía secundaria de desvío.
     */
    fun ejecutarAnimacionDesvio(onCompletado: () -> Unit) {
        iniciarSecuenciaFinal(nuevoEstado = 1, onCompletado = onCompletado)
    }

    /**
     * Activa la secuencia animada de derrota donde la locomotora alcanza al jugador.
     */
    fun ejecutarAnimacionImpacto(onCompletado: () -> Unit) {
        iniciarSecuenciaFinal(nuevoEstado = 2, onCompletado = onCompletado)
    }

    /**
     * Detiene por completo el ciclo de animación para liberar recursos al cambiar de escena.
     */
    fun detener() {
        timeline?.stop()
        timeline = null
    }

    private fun iniciarSecuenciaFinal(nuevoEstado: Int, onCompletado: () -> Unit) {
        tiempoSecuenciaFinal = 0.0
        secuenciaCompletada = false
        onSecuenciaCompletada = onCompletado
        if (timeline == null) {
            iniciarPersecucion()
        }
        estado = nuevoEstado
    }

    private fun actualizarEstado(delta: Double) {
        val w = if (canvas.width > 0) canvas.width else 400.0
        val h = if (canvas.height > 0) canvas.height else 300.0

        avanzarCapasParallax(delta, w)
        when (estado) {
            1 -> actualizarEstadoDesvio(delta, h)
            2 -> actualizarEstadoImpacto(delta)
            else -> actualizarEstadoPersecucion()
        }

        val yViaPrincipal = h * 0.76
        trenCarbon.actualizarEfectos(delta, w * trenProgresoX, yViaPrincipal + trenDesvioY, estado)
    }

    private fun avanzarCapasParallax(delta: Double, w: Double) {
        val factorVelocidad = when (estado) {
            1 -> (1.0 - (tiempoSecuenciaFinal / 2.6).coerceIn(0.0, 0.7))
            2 -> 1.6
            else -> 1.0
        }
        offsetMontanas = (offsetMontanas + 22.0 * factorVelocidad * delta) % (w + 200.0)
        offsetArboles = (offsetArboles + 85.0 * factorVelocidad * delta) % 180.0
        offsetVias = (offsetVias + 210.0 * factorVelocidad * delta) % 24.0
    }

    private fun actualizarEstadoPersecucion() {
        val oscilacion = sin(tiempoTotal * 3.0) * 0.012
        trenProgresoX = 0.05 + (progresoTiempo * 0.31) + oscilacion
        trenDesvioY = 0.0
        trenAngulo = 0.0
        temblorCamara = 0.6 + progresoTiempo * 1.4
    }

    private fun actualizarEstadoDesvio(delta: Double, h: Double) {
        tiempoSecuenciaFinal += delta
        trenProgresoX += 0.34 * delta
        if (trenProgresoX >= 0.28) {
            val avance = ((trenProgresoX - 0.28) / 0.28).coerceIn(0.0, 1.0)
            trenDesvioY = -h * 0.19 * avance
            trenAngulo = if (avance < 0.85) -12.0 * sin(avance * Math.PI) else 0.0
        }
        temblorCamara = 1.2
        verificarFinSecuencia(limiteSegundos = 2.5)
    }

    private fun actualizarEstadoImpacto(delta: Double) {
        tiempoSecuenciaFinal += delta
        if (trenProgresoX < 0.67) {
            trenProgresoX += 0.48 * delta
            temblorCamara = Random.nextDouble(2.5, 5.5)
        } else {
            trenProgresoX += 0.12 * delta
            temblorCamara = if (tiempoSecuenciaFinal < 2.0) Random.nextDouble(6.0, 11.0) else 1.5
        }
        verificarFinSecuencia(limiteSegundos = 2.3)
    }

    private fun verificarFinSecuencia(limiteSegundos: Double) {
        if (tiempoSecuenciaFinal >= limiteSegundos && !secuenciaCompletada) {
            secuenciaCompletada = true
            onSecuenciaCompletada?.invoke()
        }
    }

    private fun renderizar() {
        val gc = canvas.graphicsContext2D
        val w = canvas.width
        val h = canvas.height
        if (w <= 0 || h <= 0) return

        val shakeX = if (temblorCamara > 0) (Random.nextDouble() - 0.5) * temblorCamara * 2.0 else 0.0
        val shakeY = if (temblorCamara > 0) (Random.nextDouble() - 0.5) * temblorCamara * 2.0 else 0.0

        gc.save()
        gc.translate(shakeX, shakeY)

        renderizadorEntorno.dibujarEntornoYVias(gc, w, h, offsetMontanas, offsetArboles, offsetVias, estado)
        dibujarTrenYJugador(gc, w, h * 0.76)
        trenCarbon.dibujarParticulas(gc)

        if (estado == 2 && tiempoSecuenciaFinal in 0.65..1.25) {
            gc.fill = Color.rgb(255, 40, 20, 0.38)
            gc.fillRect(0.0, 0.0, w, h)
        }

        gc.restore()
    }

    private fun dibujarTrenYJugador(gc: GraphicsContext, w: Double, yViaPrincipal: Double) {
        val xTren = w * trenProgresoX
        val yTren = yViaPrincipal + trenDesvioY
        val xJugador = w * 0.74
        val festejando = estado == 1 && trenProgresoX > 0.52

        if (estado == 1) {
            dibujarTrenRotado(gc, xTren, yTren)
            renderizadorEntorno.dibujarJugador(gc, xJugador, yViaPrincipal - 6.0, tiempoTotal, festejando)
        } else {
            val alcanzado = estado == 2 && (trenProgresoX + 0.22 >= 0.72)
            if (!alcanzado) {
                renderizadorEntorno.dibujarJugador(gc, xJugador, yViaPrincipal - 6.0, tiempoTotal, false)
            }
            dibujarTrenRotado(gc, xTren, yTren)
        }
    }

    private fun dibujarTrenRotado(gc: GraphicsContext, xTren: Double, yTren: Double) {
        gc.save()
        gc.translate(xTren + 60.0, yTren - 24.0)
        gc.rotate(trenAngulo)
        trenCarbon.dibujarLocomotora(gc, -60.0, -24.0, tiempoTotal)
        gc.restore()
    }
}
