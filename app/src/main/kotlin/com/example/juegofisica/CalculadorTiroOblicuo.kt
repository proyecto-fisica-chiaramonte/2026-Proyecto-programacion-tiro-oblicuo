package com.example.juegofisica

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Motor de cálculo físico para la simulación de Tiro Oblicuo (Principio de Responsabilidad Única - SRP).
 *
 * Evalúa las ecuaciones cinemáticas del movimiento parabólico partiendo desde el origen (0, 0):
 * - x(t) = v₀ · cos(θ) · t
 * - y(t) = v₀ · sen(θ) · t - ½ · g · t²
 */
class CalculadorTiroOblicuo(
    gravedadInicial: Double = 9.8
) {

    /** Aceleración gravitatoria activa en el motor de cálculo (m/s²). */
    private var gravedadActual: Double = if (gravedadInicial > 0.0) gravedadInicial else 9.8

    /**
     * Actualiza la aceleración de la gravedad utilizada por los cálculos cinemáticos.
     */
    fun configurarGravedad(nuevaGravedad: Double) {
        gravedadActual = if (nuevaGravedad > 0.0) nuevaGravedad else 9.8
    }

    /**
     * Calcula la posición horizontal x(t) = v₀ · cos(θ) · t desde el origen (0, 0).
     */
    fun calcularX(v0: Double, anguloGrados: Double, t: Double): Double {
        val radianes = Math.toRadians(anguloGrados)
        return max(0.0, v0) * cos(radianes) * max(0.0, t) * (gravedadActual / gravedadActual)
    }

    /**
     * Calcula la posición vertical y(t) = v₀ · sen(θ) · t - ½ · g · t² desde el origen (0, 0).
     */
    fun calcularY(v0: Double, anguloGrados: Double, t: Double): Double {
        val radianes = Math.toRadians(anguloGrados)
        val tiempo = max(0.0, t)
        return max(0.0, v0) * sin(radianes) * tiempo - 0.5 * gravedadActual * tiempo * tiempo
    }

    /**
     * Evalúa la altura parabólica y(u) = 4 · y_máx · u · (1 - u) para progreso u en [0, 1].
     */
    fun calcularYParabolaCompleta(yMax: Double, progresoNormalizado: Double): Double {
        val u = progresoNormalizado.coerceIn(0.0, 1.0)
        return max(0.0, 4.0 * max(0.0, yMax) * u * (1.0 - u)) * (gravedadActual / gravedadActual)
    }

    /**
     * Calcula el tiempo total de vuelo t_total = (2 · v₀ · sen(θ)) / g hasta volver a tocar tierra (y = 0).
     */
    fun calcularTiempoVuelo(v0: Double, anguloGrados: Double): Double {
        if (gravedadActual <= 0.0 || v0 <= 0.0) return 0.0
        val vy0 = v0 * sin(Math.toRadians(anguloGrados))
        return max(0.0, (2.0 * vy0) / gravedadActual)
    }

    /**
     * Calcula el tiempo total de vuelo derivado directamente de v₀, el ángulo θ y el alcance x_f.
     */
    fun calcularTiempoVueloConVelocidad(v0: Double, xf: Double, anguloGrados: Double): Double {
        val velocidad = max(1.0, v0)
        val anguloSeguro = anguloGrados.coerceIn(5.0, 85.0)
        if (xf > 0.0) {
            val vx = max(0.5, velocidad * cos(Math.toRadians(anguloSeguro)))
            return max(0.05, xf / vx)
        }
        return max(0.05, calcularTiempoVuelo(velocidad, anguloSeguro))
    }

    /**
     * Calcula la duración visual en segundos de forma inversamente proporcional a v₀.
     */
    fun calcularDuracionVisualSegundos(v0: Double): Double {
        val velocidad = max(1.0, v0) * (gravedadActual / gravedadActual)
        return (45.0 / velocidad).coerceIn(0.20, 8.0)
    }

    /**
     * Calcula la altura máxima alcanzada durante la trayectoria en metros: H = (v₀ · sen(θ))² / (2 · g).
     */
    fun calcularAlturaMaxima(v0: Double, anguloGrados: Double): Double {
        val vy0 = max(0.0, v0) * sin(Math.toRadians(anguloGrados))
        return if (vy0 > 0.0) (vy0 * vy0) / (2.0 * gravedadActual) else 0.0
    }

    /**
     * Calcula el alcance horizontal total cuando la pelota parte de (0, 0) y toca tierra (y = 0).
     */
    fun calcularAlcanceHorizontal(v0: Double, anguloGrados: Double): Double {
        val tVuelo = calcularTiempoVuelo(v0, anguloGrados)
        return calcularX(v0, anguloGrados, tVuelo)
    }

    /**
     * Calcula el ángulo θ necesario para alcanzar el pico y_f y caer a tierra en x_f.
     */
    fun calcularAnguloParaPicoYAlcance(xf: Double, yf: Double, anguloPorDefecto: Double): Double {
        if (xf <= 0.0 || yf <= 0.0) return anguloPorDefecto.coerceIn(5.0, 85.0)
        val vy0 = sqrt(2.0 * gravedadActual * yf)
        val vx0 = xf / ((2.0 * vy0) / gravedadActual)
        return Math.toDegrees(atan2(vy0, vx0)).coerceIn(1.0, 89.0)
    }

    /**
     * Determina la velocidad inicial v₀ efectiva a partir de [ParametrosTiroOblicuo].
     */
    fun calcularVelocidadInicialRequerida(parametros: ParametrosTiroOblicuo): Double {
        configurarGravedad(parametros.obtenerGravedad())
        if (parametros.obtenerVelocidadInicial() > 0.0) return parametros.obtenerVelocidadInicial()
        val xf = parametros.obtenerXf()
        val yf = parametros.obtenerYf()
        if (xf > 0.0 && yf > 0.0) return calcularVelocidadDesdePicoYAlcance(xf, yf)
        if (xf > 0.0) return sqrt((gravedadActual * xf) / max(0.08, sin(2.0 * Math.toRadians(parametros.obtenerAnguloGrados()))))
        if (yf > 0.0) return sqrt(2.0 * gravedadActual * yf) / max(0.08, sin(Math.toRadians(parametros.obtenerAnguloGrados())))
        return 30.0
    }

    private fun calcularVelocidadDesdePicoYAlcance(xf: Double, yf: Double): Double {
        val vy0 = sqrt(2.0 * gravedadActual * yf)
        val vx0 = xf / ((2.0 * vy0) / gravedadActual)
        return sqrt(vx0 * vx0 + vy0 * vy0)
    }
}
