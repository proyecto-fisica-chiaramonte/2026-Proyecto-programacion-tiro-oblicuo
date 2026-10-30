package com.example.juegofisica

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.util.Duration

/**
 * Gestor del temporizador de cuenta regresiva para las partidas del juego.
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
     * Útil para inicializar el temporizador y ejecutar pruebas unitarias aisladas.
     *
     * @param segundos duración del temporizador en segundos.
     */
    fun configurarTiempo(segundos: Int) {
        detener()
        tiempoInicialSegundos = segundos
        tiempoRestanteSegundos = segundos
    }

    /**
     * Inicia la cuenta regresiva con la cantidad de segundos especificada.
     * Si ya había un temporizador corriendo, se detiene antes de comenzar.
     *
     * @param segundos duración total del temporizador en segundos (por defecto 60 segundos).
     */
    fun iniciar(segundos: Int = 60) {
        configurarTiempo(segundos)

        timeline = Timeline(
            KeyFrame(Duration.seconds(1.0), {
                decrementarSegundo()
            })
        ).apply {
            cycleCount = Timeline.INDEFINITE
            play()
        }
    }

    /**
     * Decrementa un segundo en la cuenta regresiva y dispara los callbacks correspondientes.
     * Método público para facilitar pruebas unitarias sin depender de la ejecución del hilo gráfico.
     *
     * @return true si aún queda tiempo restante, o false si el tiempo se agotó.
     */
    fun decrementarSegundo(): Boolean {
        if (tiempoRestanteSegundos > 0) {
            tiempoRestanteSegundos--
            onTick?.invoke(tiempoRestanteSegundos, formatearTiempo(tiempoRestanteSegundos))
            if (tiempoRestanteSegundos == 0) {
                detener()
                onFin?.invoke()
                return false
            }
            return true
        } else {
            detener()
            onFin?.invoke()
            return false
        }
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
     *
     * @param segundos tiempo a formatear (por defecto, los segundos restantes).
     * @return cadena con el formato "mm:ss".
     */
    fun formatearTiempo(segundos: Int = tiempoRestanteSegundos): String {
        val totalSegundos = segundos.coerceAtLeast(0)
        val minutos = totalSegundos / 60
        val segs = totalSegundos % 60
        return String.format("%02d:%02d", minutos, segs)
    }
}
