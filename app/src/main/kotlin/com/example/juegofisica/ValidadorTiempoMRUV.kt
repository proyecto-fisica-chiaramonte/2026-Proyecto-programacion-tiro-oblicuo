package com.example.juegofisica

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Validador de respuestas para el cálculo del tiempo en problemas de MRUV del modo fácil.
 *
 * Aplica el Principio de Responsabilidad Única (SRP) aislando el cálculo físico
 * según la fórmula t = √(2 · d / a) y la validación numérica con tolerancia decimal.
 *
 * @property tolerancia margen de error admisible para tolerar diferencias de redondeo (por defecto ±0.1).
 */
class ValidadorTiempoMRUV(
    private val tolerancia: Double = 0.1
) {

    /**
     * Calcula el tiempo teórico t a partir de la distancia d y la aceleración a:
     * t = √(2 · d / a).
     *
     * @param distancia distancia recorrida d en metros (debe ser >= 0).
     * @param aceleracion aceleración a en m/s² (debe ser > 0).
     * @return tiempo teórico en segundos, o 0.0 si los parámetros son inválidos.
     */
    fun calcularTiempo(distancia: Double, aceleracion: Double): Double {
        if (aceleracion <= 0.0 || distancia < 0.0) {
            return 0.0
        }
        return sqrt(2.0 * distancia / aceleracion)
    }

    /**
     * Compara el valor numérico ingresado por el usuario con el valor teórico esperado,
     * admitiendo una tolerancia decimal para evitar fallos por redondeo.
     *
     * Normaliza comas a puntos para soportar formatos numéricos locales e internacionales.
     *
     * @param entradaUsuario texto ingresado en el campo de respuesta.
     * @param valorTeorico resultado teórico esperado en segundos.
     * @return true si la entrada es numérica y la diferencia absoluta es menor o igual a la tolerancia.
     */
    fun validar(entradaUsuario: String, valorTeorico: Double): Boolean {
        val textoNormalizado = entradaUsuario.trim().replace(',', '.')
        val valorIngresado = textoNormalizado.toDoubleOrNull() ?: return false
        return abs(valorIngresado - valorTeorico) <= tolerancia
    }
}
