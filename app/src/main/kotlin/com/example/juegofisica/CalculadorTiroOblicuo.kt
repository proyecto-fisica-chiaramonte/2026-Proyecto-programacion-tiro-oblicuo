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
 * respetando tanto la velocidad inicial (v₀) ingresada por el usuario como las coordenadas objetivo (x_f, y_f).
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
     * Evalúa la altura parabólica y(t) en función del progreso normalizado u = t / t_total (0 <= u <= 1),
     * garantizando que la pelota suba exactamente hasta la altura máxima yMax en u = 0.5 y vuelva a
     * tocar tierra (y = 0) en u = 1.0:
     *
     * Ecuación equivalente a v₀·sen(θ)·t - ½·g·t²: y(u) = 4 · y_máx · u · (1 - u)
     */
    fun calcularYParabolaCompleta(yMax: Double, progresoNormalizado: Double): Double {
        val u = progresoNormalizado.coerceIn(0.0, 1.0)
        return max(0.0, 4.0 * max(0.0, yMax) * u * (1.0 - u))
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
     * Calcula el tiempo total de vuelo derivado directamente de la velocidad inicial v₀, el ángulo θ
     * y el alcance objetivo x_f: t_total = x_f / (v₀ · cos(θ)).
     * Si no se especifica x_f (> 0), emplea el tiempo de vuelo clásico 2·v₀·sen(θ)/g.
     */
    fun calcularTiempoVueloConVelocidad(
        v0: Double,
        xf: Double,
        anguloGrados: Double,
        gravedad: Double
    ): Double {
        val velocidad = max(1.0, v0)
        val anguloSeguro = anguloGrados.coerceIn(5.0, 85.0)
        if (xf > 0.0) {
            val vx = max(0.5, velocidad * cos(Math.toRadians(anguloSeguro)))
            return max(0.05, xf / vx)
        }
        return max(0.05, calcularTiempoVuelo(0.0, velocidad, anguloSeguro, gravedad))
    }

    /**
     * Calcula la duración visual de la animación en segundos de forma inversamente proporcional a v₀
     * (de modo que la velocidad visual en pantalla sea directamente proporcional a v₀).
     *
     * Ejemplos:
     * - v₀ = 10 m/s  -> ~4.50 s en pantalla (desplazamiento lento y pausado)
     * - v₀ = 30 m/s  -> ~1.50 s en pantalla (desplazamiento medio)
     * - v₀ = 100 m/s -> ~0.45 s en pantalla (desplazamiento rápido, 10x más veloz que v₀ = 10 m/s)
     */
    fun calcularDuracionVisualSegundos(v0: Double): Double {
        val velocidad = max(1.0, v0)
        return (45.0 / velocidad).coerceIn(0.20, 8.0)
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
     * Calcula la velocidad inicial v₀ (m/s): si el usuario ingresó un valor explícito v₀ > 0,
     * lo respeta directamente; si v₀ <= 0 (omitido), lo deduce de (x_f, y_f, θ, g).
     */
    fun calcularVelocidadInicialRequerida(
        v0Ingresada: Double,
        xf: Double,
        yf: Double,
        anguloGrados: Double,
        gravedad: Double
    ): Double {
        if (v0Ingresada > 0.0) {
            return v0Ingresada
        }
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
        return 30.0
    }
}
