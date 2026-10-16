package com.example.juegofisica

import kotlin.math.abs

/**
 * Validador de respuestas matemáticas para problemas de MRUV en modo difícil.
 *
 * Aplica el Principio de Responsabilidad Única (SRP) aislando el cálculo físico
 * y la verificación numérica del resto de la interfaz gráfica de usuario.
 *
 * @property tolerancia margen de error admisible para tolerar diferencias de redondeo (por defecto ±0.1).
 */
class ValidadorRespuestaMRUV(
    private val tolerancia: Double = 0.1
) {

    /**
     * Calcula el tiempo t teórico a partir de la distancia d y la velocidad v:
     * t = d / v.
     *
     * @param distancia distancia d recorrida en metros.
     * @param velocidad velocidad v en m/s.
     * @return tiempo t en segundos, o 0.0 si los parámetros son inválidos.
     */
    fun calcularTiempo(distancia: Double, velocidad: Double): Double {
        if (velocidad <= 0.0 || distancia < 0.0) {
            return 0.0
        }
        return distancia / velocidad
    }

    /**
     * Calcula la posición final teórica x(t) usando las fórmulas del MRUV:
     * t = d / v
     * x(t) = x₀ + v₀ · t + ½ · a · t²
     *
     * @param posicionInicial posición inicial x₀ en metros.
     * @param velocidadInicial velocidad inicial v₀ en m/s.
     * @param aceleracion aceleración a en m/s².
     * @param distancia distancia d en metros.
     * @param velocidad velocidad v en m/s para calcular el tiempo t.
     * @return valor teórico exacto de la posición final x(t).
     */
    fun calcularPosicionFinal(
        posicionInicial: Double,
        velocidadInicial: Double,
        aceleracion: Double,
        distancia: Double,
        velocidad: Double
    ): Double {
        val t = calcularTiempo(distancia, velocidad)
        return posicionInicial + (velocidadInicial * t) + (0.5 * aceleracion * t * t)
    }

    /**
     * Compara el valor ingresado como texto por el usuario contra el valor teórico exacto,
     * admitiendo una tolerancia decimal para evitar fallos por redondeo.
     *
     * Normaliza comas a puntos para soportar formatos numéricos en español e inglés.
     *
     * @param entradaUsuario texto ingresado en el campo de respuesta.
     * @param valorTeorico resultado exacto esperado.
     * @return true si la entrada es numérica y la diferencia absoluta es menor o igual a la tolerancia.
     */
    fun validar(entradaUsuario: String, valorTeorico: Double): Boolean {
        val textoNormalizado = entradaUsuario.trim().replace(',', '.')
        val valorIngresado = textoNormalizado.toDoubleOrNull() ?: return false
        return abs(valorIngresado - valorTeorico) <= tolerancia
    }
}
