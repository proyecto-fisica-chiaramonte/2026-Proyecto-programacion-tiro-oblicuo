package com.example.juegofisica

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.util.Duration
import kotlin.random.Random

/**
 * Controlador del ciclo de animación continua y estados del escenario para los niveles de MRUV (SRP).
 *
 * Delega el estado cinemático en [EstadoAnimacionEscenario], el trazado del entorno en
 * [RenderizadorEscenarioMRUV] y la locomotora en [TrenCarbonWidget].
 */
class AnimacionEscenario(
    esModoDificil: Boolean = true
) {

    /** Lienzo responsivo donde se dibuja el escenario cuadro a cuadro. */
    private val canvas: Canvas = Canvas(400.0, 300.0)

    /** Estado cinemático y de desplazamiento parallax del escenario (SRP). */
    private val estadoEscenario: EstadoAnimacionEscenario = EstadoAnimacionEscenario()

    /** Renderizador dedicado para el cielo, montañas, árboles, vías y jugador (SRP). */
    private val renderizadorEntorno: RenderizadorEscenarioMRUV = RenderizadorEscenarioMRUV(esModoDificil)

    /** Componente visual dedicado a la locomotora clásica de carbón y su humo (SRP). */
    private val trenCarbon: TrenCarbonWidget = TrenCarbonWidget(esModoDificil)

    /** Bucle de animación continua a 40 FPS. */
    private var timeline: Timeline? = null

    /** Callback pendiente a ejecutar una única vez cuando termina la secuencia final. */
    private var onSecuenciaCompletada: (() -> Unit)? = null

    /**
     * Devuelve el nodo Canvas del escenario animado.
     */
    fun obtenerCanvas(): Canvas = canvas

    /**
     * Vincula las dimensiones del Canvas al contenedor raíz.
     */
    fun vincularTamano(root: StackPane) {
        canvas.widthProperty().bind(root.widthProperty())
        canvas.heightProperty().bind(root.heightProperty())
    }

    /**
     * Inicia el bucle continuo de animación en modo persecución activa.
     */
    fun iniciarPersecucion() {
        estadoEscenario.reiniciarPersecucion()
        onSecuenciaCompletada = null
        timeline?.stop()
        timeline = Timeline(KeyFrame(Duration.millis(25.0), { avanzarCuadro(0.025) })).apply {
            cycleCount = Timeline.INDEFINITE
            play()
        }
    }

    /**
     * Actualiza el acercamiento gradual de la locomotora según el tiempo restante de la partida.
     */
    fun actualizarProgreso(progreso: Double) {
        estadoEscenario.fijarProgresoTiempo(progreso)
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
        if (timeline == null) iniciarPersecucion()
        estadoEscenario.activarModoFinal(nuevoEstado)
        onSecuenciaCompletada = onCompletado
    }

    private fun avanzarCuadro(delta: Double) {
        val w = if (canvas.width > 0) canvas.width else 400.0
        val h = if (canvas.height > 0) canvas.height else 300.0
        estadoEscenario.avanzarCuadro(delta, w, h)
        val xTren = w * estadoEscenario.obtenerCinematicaTren(0)
        val yTren = h * 0.76 + estadoEscenario.obtenerCinematicaTren(1)
        trenCarbon.actualizarEfectos(delta, doubleArrayOf(xTren, yTren), estadoEscenario.obtenerEstado())
        if (estadoEscenario.cumplioTiempoFinal() && onSecuenciaCompletada != null) {
            val callback = onSecuenciaCompletada
            onSecuenciaCompletada = null
            callback?.invoke()
        }
        renderizar(w, h)
    }

    private fun renderizar(w: Double, h: Double) {
        val gc = canvas.graphicsContext2D
        val temblor = estadoEscenario.obtenerCinematicaTren(3)
        gc.save()
        gc.translate((Random.nextDouble() - 0.5) * temblor * 2.0, (Random.nextDouble() - 0.5) * temblor * 2.0)
        renderizadorEntorno.dibujarEntornoYVias(gc, estadoEscenario, doubleArrayOf(w, h))
        dibujarTrenYJugador(gc, w, h * 0.76)
        trenCarbon.dibujarParticulas(gc)
        if (estadoEscenario.obtenerEstado() == 2 && estadoEscenario.obtenerTiempoFinal() in 0.65..1.25) {
            gc.fill = Color.rgb(255, 40, 20, 0.38)
            gc.fillRect(0.0, 0.0, w, h)
        }
        gc.restore()
    }

    private fun dibujarTrenYJugador(gc: GraphicsContext, w: Double, yViaPrincipal: Double) {
        val xTren = w * estadoEscenario.obtenerCinematicaTren(0)
        val yTren = yViaPrincipal + estadoEscenario.obtenerCinematicaTren(1)
        val Modo = estadoEscenario.obtenerEstado()
        val visibleAntes = Modo != 1 && !(Modo == 2 && estadoEscenario.obtenerCinematicaTren(0) >= 0.50)
        if (visibleAntes) renderizadorEntorno.dibujarJugador(gc, estadoEscenario)
        gc.save()
        gc.translate(xTren + 60.0, yTren - 24.0)
        gc.rotate(estadoEscenario.obtenerCinematicaTren(2))
        trenCarbon.dibujarLocomotora(gc, estadoEscenario.obtenerTiempoTotal())
        gc.restore()
        if (Modo == 1) renderizadorEntorno.dibujarJugador(gc, estadoEscenario)
    }
}
