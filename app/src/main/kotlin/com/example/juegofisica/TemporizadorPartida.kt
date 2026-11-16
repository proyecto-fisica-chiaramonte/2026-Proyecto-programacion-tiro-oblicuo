package com.example.juegofisica

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.util.Duration

/**
 * Gestor del temporizador de cuenta regresiva para las partidas del juego (SRP).
 *
 * Encapsula de forma privada el estado temporal (inicial y restante), el bucle Timeline
 * y las notificaciones por segundo transcurrido y por finalización del tiempo.
 */
class TemporizadorPartida(
    private val onTick: ((segundosRestantes: Int, tiempoFormateado: String) -> Unit)? = null,
    private val onFin: (() -> Unit)? = null
) {

    /** Estado interno de tiempo: [tiempoInicialSegundos, tiempoRestanteSegundos]. */
    private val estadoTiempo: IntArray = intArrayOf(60, 60)

    /** Instancia subyacente de Timeline para la animación por segundo de JavaFX. */
    private var timeline: Timeline? = null

    /** Devuelve el tiempo inicial configurado para la partida en segundos. */
    fun obtenerTiempoInicial(): Int = estadoTiempo[0]

    /** Devuelve los segundos restantes en la cuenta regresiva actual. */
    fun obtenerTiempoRestante(): Int = estadoTiempo[1]

    /** Devuelve los segundos empleados desde el inicio de la partida. */
    fun obtenerTiempoEmpleado(): Int = (estadoTiempo[0] - estadoTiempo[1]).coerceAtLeast(0)

    /**
     * Configura el tiempo inicial y restante de la partida sin iniciar la animación.
     */
    fun configurarTiempo(segundos: Int) {
        detener()
        val valor = segundos.coerceAtLeast(0)
        estadoTiempo[0] = valor
        estadoTiempo[1] = valor
    }

    /**
     * Inicia la cuenta regresiva con la cantidad de segundos especificada.
     */
    fun iniciar(segundos: Int = 60) {
        configurarTiempo(segundos)
        timeline = Timeline(KeyFrame(Duration.seconds(1.0), { decrementarSegundo() })).apply {
            cycleCount = Timeline.INDEFINITE
            play()
        }
    }

    /**
     * Decrementa un segundo en la cuenta regresiva y dispara los callbacks correspondientes.
     */
    fun decrementarSegundo(): Boolean {
        if (estadoTiempo[1] <= 0) {
            detener()
            onFin?.invoke()
            return false
        }
        estadoTiempo[1]--
        onTick?.invoke(estadoTiempo[1], formatearTiempo(estadoTiempo[1]))
        if (estadoTiempo[1] == 0) {
            detener()
            onFin?.invoke()
            return false
        }
        return true
    }

    /**
     * Detiene la cuenta regresiva si está activa.
     */
    fun detener() {
        timeline?.stop()
        timeline = null
        estadoTiempo[0] = estadoTiempo[0].coerceAtLeast(0)
    }

    /**
     * Formatea una cantidad de segundos al formato de reloj mm:ss.
     */
    fun formatearTiempo(segundos: Int = estadoTiempo[1]): String {
        val total = segundos.coerceAtLeast(0)
        return String.format("%02d:%02d", total / 60, total % 60)
    }
}
