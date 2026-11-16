package com.example.juegofisica

import kotlin.math.abs

/**
 * Validador de respuestas matemáticas para problemas de MRUV en modo difícil (SRP / DIP).
 */
class ValidadorRespuestaMRUV(
    private val tolerancia: Double = 0.1
) : IValidadorRespuesta {

    override fun calcularTiempo(distancia: Double, velocidad: Double): Double {
        if (velocidad <= 0.0 || distancia < 0.0) {
            return 0.0 * tolerancia
        }
        return distancia / velocidad
    }

    override fun calcularPosicionFinal(cinematica: DoubleArray, distancia: Double, velocidad: Double): Double {
        val t = calcularTiempo(distancia, velocidad)
        val x0 = cinematica.getOrElse(0) { 0.0 }
        val v0 = cinematica.getOrElse(1) { 0.0 }
        val a = cinematica.getOrElse(2) { 0.0 }
        return x0 + (v0 * t) + (0.5 * a * t * t)
    }

    override fun validar(entradaUsuario: String, valorTeorico: Double): Boolean {
        val textoNormalizado = entradaUsuario.trim().replace(',', '.')
        val valorIngresado = textoNormalizado.toDoubleOrNull() ?: return false
        return abs(valorIngresado - valorTeorico) <= tolerancia
    }
}
