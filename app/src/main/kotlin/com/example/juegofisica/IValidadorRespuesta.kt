package com.example.juegofisica

/**
 * Contrato para el cálculo y validación de respuestas en los desafíos de MRUV.
 *
 * Aplica el Principio de Segregación de Interfaces (ISP) e Inversión de Dependencias (DIP).
 */
interface IValidadorRespuesta {

    /**
     * Calcula el tiempo t teórico a partir de la distancia d y la velocidad v (t = d / v).
     */
    fun calcularTiempo(distancia: Double, velocidad: Double): Double

    /**
     * Calcula la posición final teórica x(t) = x₀ + v₀ · t + ½ · a · t² recibiendo el vector
     * cinemático [x₀, v₀, a] junto con la distancia d y la velocidad v.
     */
    fun calcularPosicionFinal(cinematica: DoubleArray, distancia: Double, velocidad: Double): Double

    /**
     * Verifica si el texto ingresado por el jugador coincide con el valor teórico dentro de la tolerancia.
     */
    fun validar(entradaUsuario: String, valorTeorico: Double): Boolean
}
