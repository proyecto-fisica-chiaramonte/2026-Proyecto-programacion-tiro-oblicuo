package com.example.juegofisica

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Motor de cálculo físico para la simulación de Tiro Oblicuo (Principio de Responsabilidad Única - SRP).
 *
 * Evalúa las ecuaciones cinemáticas exactas del movimiento parabólico partiendo desde el origen (0, 0):
 * - x(t) = v₀ · cos(θ) · t
 * - y(t) = v₀ · sen(θ) · t - ½ · g · t²
 * y calcula automáticamente la velocidad inicial (v₀) y el ángulo (θ) necesarios para que el proyectil
 * alcance una altura máxima (pico) y_f y caiga a tierra (y = 0) exactamente en el alcance horizontal x_f.
 */
class CalculadorTiroOblicuo {

    /**
     * Calcula la posición horizontal x(t) en metros para el instante t (en segundos).
     *
     * Ecuación: x(t) = x₀ + v₀ · cos(θ) · t
     */
    fun calcularX(x0: Double, v0: Double, anguloGrados: Double, t: Double): Double {
        val radianes = Math.toRadians(anguloGrados)
        return x0 + v0 * cos(radianes) * t
    }

    /**
     * Calcula la posición vertical y(t) en metros para el instante t (en segundos).
     *
     * Ecuación: y(t) = y₀ + v₀ · sen(θ) · t - ½ · g · t²
     */
    fun calcularY(y0: Double, v0: Double, anguloGrados: Double, gravedad: Double, t: Double): Double {
        val radianes = Math.toRadians(anguloGrados)
        return y0 + v0 * sin(radianes) * t - 0.5 * gravedad * t * t
    }

    /**
     * Calcula el tiempo total de vuelo t_total en segundos hasta que la pelota vuelve a tocar tierra (y = 0).
     */
    fun calcularTiempoVuelo(y0: Double, v0: Double, anguloGrados: Double, gravedad: Double): Double {
        if (gravedad <= 0.0) return 0.0
        val alturaInicial = max(0.0, y0)
        if (v0 <= 0.0 && alturaInicial <= 0.0) return 0.0
        val vy0 = max(0.0, v0) * sin(Math.toRadians(anguloGrados))
        val discriminante = vy0 * vy0 + 2.0 * gravedad * alturaInicial
        if (discriminante < 0.0) return 0.0
        val tiempo = (vy0 + sqrt(discriminante)) / gravedad
        return max(0.0, tiempo)
    }

    /**
     * Calcula la altura máxima alcanzada durante la trayectoria en metros.
     */
    fun calcularAlturaMaxima(y0: Double, v0: Double, anguloGrados: Double, gravedad: Double): Double {
        val alturaInicial = max(0.0, y0)
        if (gravedad <= 0.0) return alturaInicial
        val vy0 = max(0.0, v0) * sin(Math.toRadians(anguloGrados))
        return if (vy0 > 0.0) {
            alturaInicial + (vy0 * vy0) / (2.0 * gravedad)
        } else {
            alturaInicial
        }
    }

    /**
     * Calcula el alcance horizontal total cuando la pelota parte desde (0, y₀) y vuelve a tocar tierra (y = 0).
     */
    fun calcularAlcanceHorizontal(
        y0: Double,
        v0: Double,
        anguloGrados: Double,
        gravedad: Double
    ): Double {
        if (gravedad <= 0.0 || v0 <= 0.0) return 0.0
        val tVuelo = calcularTiempoVuelo(y0, v0, anguloGrados, gravedad)
        return calcularX(0.0, v0, anguloGrados, tVuelo)
    }

    /**
     * Calcula el ángulo de lanzamiento θ (en grados) necesario para que una parábola que parte
     * de (0, 0) alcance una altura máxima (pico) de y_f metros y caiga a tierra (y = 0) en x_f metros.
     *
     * Relación física exacta: tan(θ) = (4 · y_f) / x_f
     */
    fun calcularAnguloParaPicoYAlcance(xf: Double, yf: Double, anguloPorDefecto: Double): Double {
        if (xf <= 0.0 || yf <= 0.0) {
            return anguloPorDefecto.coerceIn(5.0, 85.0)
        }
        val vy0 = sqrt(2.0 * 9.8 * yf)
        val tTotal = (2.0 * vy0) / 9.8
        val vx0 = xf / tTotal
        return Math.toDegrees(atan2(vy0, vx0)).coerceIn(1.0, 89.0)
    }

    /**
     * Calcula la velocidad inicial v₀ (m/s) necesaria para que el proyectil:
     * - Si se especifican x_f > 0 e y_f > 0: suba hasta el pico y_f y caiga en x_f (y = 0).
     * - Si sólo se especifica x_f > 0 (con y_f <= 0): alcance horizontalmente x_f con el ángulo θ dado.
     * - Si sólo se especifica y_f > 0 (con x_f <= 0): suba hasta la altura máxima y_f con el ángulo θ dado.
     */
    fun calcularVelocidadInicialRequerida(
        v0Ingresada: Double,
        xf: Double,
        yf: Double,
        anguloGrados: Double,
        gravedad: Double
    ): Double {
        val g = if (gravedad > 0.0) gravedad else 9.8
        if (xf > 0.0 && yf > 0.0) {
            val vy0 = sqrt(2.0 * g * yf)
            val tTotal = (2.0 * vy0) / g
            val vx0 = xf / tTotal
            return sqrt(vx0 * vx0 + vy0 * vy0)
        }
        if (xf > 0.0) {
            val rad = Math.toRadians(anguloGrados.coerceIn(5.0, 85.0))
            val sen2Theta = max(0.08, sin(2.0 * rad))
            return sqrt((g * xf) / sen2Theta)
        }
        if (yf > 0.0) {
            val rad = Math.toRadians(anguloGrados.coerceIn(5.0, 85.0))
            val senTheta = max(0.08, sin(rad))
            val vy0 = sqrt(2.0 * g * yf)
            return vy0 / senTheta
        }
        return if (v0Ingresada > 0.0) v0Ingresada else 30.0
    }
}
