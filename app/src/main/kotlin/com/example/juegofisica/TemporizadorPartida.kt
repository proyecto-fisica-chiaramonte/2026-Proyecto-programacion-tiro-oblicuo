package com.example.juegofisica

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.util.Duration

/**
 * Gestor del temporizador de cuenta regresiva para las partidas del juego (SRP).
 *
 * Encapsula la lógica de cuenta atrás con JavaFX Timeline, el formateo en "mm:ss",
 * y las notificaciones por segundo transcurrido y por finalización del tiempo.
 */
class TemporizadorPartida(
    private val onTick: ((segundosRestantes: Int, tiempoFormateado: String) -> Unit)? = null,
    private val onFin: (() -> Unit)? = null
) {

    /** Tiempo inicial configurado para la cuenta regresiva en segundos (por defecto 60s = 1 minuto). */
    var tiempoInicialSegundos: Int = 60
        private set

    /** Segundos restantes en la cuenta regresiva actual. */
    var tiempoRestanteSegundos: Int = 60
        private set

    /** Segundos transcurridos desde el inicio de la cuenta regresiva. */
    val tiempoEmpleadoSegundos: Int
        get() = (tiempoInicialSegundos - tiempoRestanteSegundos).coerceAtLeast(0)

    /** Instancia subyacente de Timeline para la animación por segundo de JavaFX. */
    private var timeline: Timeline? = null

    /**
     * Configura el tiempo inicial y restante de la partida sin iniciar la animación de Timeline.
     */
    fun configurarTiempo(segundos: Int) {
        detener()
        tiempoInicialSegundos = segundos
        tiempoRestanteSegundos = segundos
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
        if (tiempoRestanteSegundos <= 0) {
            finalizarCuentaRegresiva()
            return false
        }
        tiempoRestanteSegundos--
        onTick?.invoke(tiempoRestanteSegundos, formatearTiempo(tiempoRestanteSegundos))
        if (tiempoRestanteSegundos == 0) {
            finalizarCuentaRegresiva()
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
    }

    /**
     * Formatea una cantidad de segundos al formato de reloj mm:ss.
     */
    fun formatearTiempo(segundos: Int = tiempoRestanteSegundos): String {
        val total = segundos.coerceAtLeast(0)
        return String.format("%02d:%02d", total / 60, total % 60)
    }

    private fun finalizarCuentaRegresiva() {
        detener()
        onFin?.invoke()
    }
}
