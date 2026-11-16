package com.example.juegofisica

import kotlin.math.sqrt

/**
 * Validador de respuestas para el cálculo del tiempo (t) en problemas de MRUV del modo fácil (SRP / DRY).
 *
 * Evalúa la fórmula t = √(2 · d / a) y reutiliza la comparación numérica con tolerancia de [ValidadorRespuestaMRUV].
 *
 * @property tolerancia margen de error admisible para tolerar diferencias de redondeo (por defecto ±0.1).
 */
class ValidadorTiempoMRUV(
    tolerancia: Double = 0.1
) {

    /** Validador numérico compartido para evitar duplicar el parseo y control de tolerancia (DRY). */
    private val validadorNumerico: IValidadorRespuesta = ValidadorRespuestaMRUV(tolerancia)

    /**
     * Calcula el tiempo teórico t a partir de la distancia d y la aceleración a:
     * t = √(2 · d / a).
     */
    fun calcularTiempo(distancia: Double, aceleracion: Double): Double {
        if (aceleracion <= 0.0 || distancia < 0.0) {
            return 0.0
        }
        return sqrt(2.0 * distancia / aceleracion)
    }

    /**
     * Compara el valor numérico ingresado por el usuario con el valor teórico esperado.
     */
    fun validar(entradaUsuario: String, valorTeorico: Double): Boolean {
        return validadorNumerico.validar(entradaUsuario, valorTeorico)
    }
}
