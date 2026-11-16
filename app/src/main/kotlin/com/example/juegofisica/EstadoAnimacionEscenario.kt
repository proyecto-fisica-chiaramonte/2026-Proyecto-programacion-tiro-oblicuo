package com.example.juegofisica

import kotlin.math.sin
import kotlin.random.Random

/**
 * Estado cinemático y de desplazamiento parallax del escenario ferroviario de MRUV (SRP).
 *
 * Encapsula la máquina de estados (0 = persecución, 1 = desvío victoria, 2 = impacto derrota),
 * los desplazamientos de las capas parallax y las coordenadas normalizadas de la locomotora.
 */
class EstadoAnimacionEscenario {

    /** Estado actual: 0 = Persecución, 1 = Desvío (Victoria), 2 = Impacto (Derrota). */
    private var estado: Int = 0

    /** Tiempo total acumulado en segundos. */
    private var tiempoTotal: Double = 0.0

    /** Tiempo acumulado en la secuencia final (s). */
    private var tiempoSecuenciaFinal: Double = 0.0

    /** Progreso del temporizador de la partida entre 0.0 y 1.0. */
    private var progresoTiempo: Double = 0.0

    /** Desplazamiento horizontal de montañas, árboles y vías: [montañas, árboles, vías]. */
    private val offsetsParallax: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0)

    /** Estado del tren: [progresoX, desvioY, angulo, temblorCamara]. */
    private val cinematicaTren: DoubleArray = doubleArrayOf(0.06, 0.0, 0.0, 0.6)

    /**
     * Reinicia el estado para comenzar la persecución activa.
     */
    fun reiniciarPersecucion() {
        estado = 0
        tiempoSecuenciaFinal = 0.0
        cinematicaTren[1] = 0.0
        cinematicaTren[2] = 0.0
    }

    /**
     * Cambia al estado indicado (1 = desvío, 2 = impacto) reiniciando el reloj de secuencia final.
     */
    fun activarModoFinal(nuevoEstado: Int) {
        estado = nuevoEstado
        tiempoSecuenciaFinal = 0.0
    }

    /**
     * Actualiza el progreso normalizado del temporizador (0.0 a 1.0).
     */
    fun fijarProgresoTiempo(progreso: Double) {
        progresoTiempo = progreso.coerceIn(0.0, 1.0)
    }

    /**
     * Avanza un cuadro temporal (delta) actualizando parallax y cinemática del tren.
     */
    fun avanzarCuadro(delta: Double, anchoLienzo: Double, altoLienzo: Double) {
        tiempoTotal += delta
        val factor = when (estado) {
            1 -> (1.0 - (tiempoSecuenciaFinal / 2.6).coerceIn(0.0, 0.7))
            2 -> 1.6
            else -> 1.0
        }
        offsetsParallax[0] = (offsetsParallax[0] + 22.0 * factor * delta) % (anchoLienzo + 200.0)
        offsetsParallax[1] = (offsetsParallax[1] + 85.0 * factor * delta) % 180.0
        offsetsParallax[2] = (offsetsParallax[2] + 210.0 * factor * delta) % 24.0
        actualizarCinematicaTren(delta, altoLienzo)
    }

    /**
     * Indica si la secuencia final activa ya alcanzó su duración objetivo.
     */
    fun cumplioTiempoFinal(): Boolean {
        val limite = if (estado == 1) 2.5 else 2.3
        return estado > 0 && tiempoSecuenciaFinal >= limite
    }

    /** Devuelve el identificador del estado actual (0, 1 o 2). */
    fun obtenerEstado(): Int = estado

    /** Devuelve el tiempo total transcurrido en segundos. */
    fun obtenerTiempoTotal(): Double = tiempoTotal

    /** Devuelve el tiempo de la secuencia final en segundos. */
    fun obtenerTiempoFinal(): Double = tiempoSecuenciaFinal

    /** Devuelve el desplazamiento parallax de la capa solicitada (0 = montañas, 1 = árboles, 2 = vías). */
    fun obtenerOffset(indiceCapa: Int): Double = offsetsParallax[indiceCapa.coerceIn(0, 2)]

    /** Devuelve el parámetro cinemático del tren (0 = progresoX, 1 = desvioY, 2 = angulo, 3 = temblor). */
    fun obtenerCinematicaTren(indice: Int): Double = cinematicaTren[indice.coerceIn(0, 3)]

    private fun actualizarCinematicaTren(delta: Double, altoLienzo: Double) {
        if (estado == 0) {
            cinematicaTren[0] = 0.05 + (progresoTiempo * 0.31) + sin(tiempoTotal * 3.0) * 0.012
            cinematicaTren[1] = 0.0
            cinematicaTren[2] = 0.0
            cinematicaTren[3] = 0.6 + progresoTiempo * 1.4
        } else if (estado == 1) {
            avanzarDesvio(delta, altoLienzo)
        } else {
            avanzarImpacto(delta)
        }
    }

    private fun avanzarDesvio(delta: Double, altoLienzo: Double) {
        tiempoSecuenciaFinal += delta
        cinematicaTren[0] += 0.34 * delta
        if (cinematicaTren[0] >= 0.28) {
            val avance = ((cinematicaTren[0] - 0.28) / 0.28).coerceIn(0.0, 1.0)
            cinematicaTren[1] = -altoLienzo * 0.19 * avance
            cinematicaTren[2] = if (avance < 0.85) -12.0 * sin(avance * Math.PI) else 0.0
        }
        cinematicaTren[3] = 1.2
    }

    private fun avanzarImpacto(delta: Double) {
        tiempoSecuenciaFinal += delta
        val cerca = cinematicaTren[0] >= 0.67
        cinematicaTren[0] += (if (cerca) 0.12 else 0.48) * delta
        cinematicaTren[3] = if (!cerca) 4.0 else if (tiempoSecuenciaFinal < 2.0) Random.nextDouble(6.0, 10.0) else 1.5
    }
}
