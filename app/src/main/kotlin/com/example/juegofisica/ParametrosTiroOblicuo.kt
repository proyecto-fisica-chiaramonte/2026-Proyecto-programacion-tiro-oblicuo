package com.example.juegofisica

import kotlin.math.max

/**
 * Objeto de dominio que encapsula y valida los parámetros físicos de entrada para la
 * Simulación de Tiro Oblicuo (Principio de Responsabilidad Única - SRP).
 *
 * Evita el paso de múltiples parámetros primitivos sueltos entre la interfaz de usuario,
 * el lienzo de animación y el motor de cálculo matemático.
 */
class ParametrosTiroOblicuo(
    v0Inicial: Double = 30.0,
    gravedadInicial: Double = 9.8,
    anguloInicial: Double = 45.0
) {

    /** Velocidad inicial v₀ configurada (m/s, >= 0). */
    private var velocidadInicial: Double = max(0.0, v0Inicial)

    /** Aceleración de la gravedad g configurada (m/s², > 0). */
    private var gravedad: Double = if (gravedadInicial > 0.0) gravedadInicial else 9.8

    /** Ángulo de lanzamiento θ configurado en grados (1° a 89°). */
    private var anguloGrados: Double = anguloInicial.coerceIn(1.0, 89.0)

    /** Posición/alcance horizontal objetivo x_f en metros (>= 0). */
    private var xfObjetivo: Double = 0.0

    /** Altura máxima/objetivo y_f en metros (>= 0). */
    private var yfObjetivo: Double = 0.0

    /**
     * Configura las coordenadas objetivo (x_f, y_f) del lanzamiento parabólico.
     */
    fun configurarObjetivo(xf: Double, yf: Double): ParametrosTiroOblicuo {
        xfObjetivo = max(0.0, xf)
        yfObjetivo = max(0.0, yf)
        return this
    }

    /**
     * Devuelve la velocidad inicial configurada por el usuario (0.0 si debe calcularse).
     */
    fun obtenerVelocidadInicial(): Double = velocidadInicial

    /**
     * Devuelve la aceleración gravitatoria g validada (> 0).
     */
    fun obtenerGravedad(): Double = gravedad

    /**
     * Devuelve el ángulo de lanzamiento θ en grados acotado a un rango físico seguro.
     */
    fun obtenerAnguloGrados(): Double = anguloGrados.coerceIn(5.0, 85.0)

    /**
     * Devuelve el alcance horizontal objetivo x_f (m).
     */
    fun obtenerXf(): Double = xfObjetivo

    /**
     * Devuelve la altura máxima/objetivo y_f (m).
     */
    fun obtenerYf(): Double = yfObjetivo

    /**
     * Indica si el usuario definió tanto un alcance x_f > 0 como una altura y_f > 0 sin fijar v₀.
     */
    fun requiereAnguloAutomatico(): Boolean {
        return xfObjetivo > 0.0 && yfObjetivo > 0.0 && velocidadInicial <= 0.0
    }
}
