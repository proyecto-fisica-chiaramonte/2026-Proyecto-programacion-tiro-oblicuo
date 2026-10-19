package com.example.juegofisica

/**
 * Abstracción para el cálculo físico y la validación de respuestas matemáticas en los problemas de física.
 *
 * Aplica el Principio de Inversión de Dependencias (DIP) y de Segregación de Interfaces (ISP),
 * desacoplando la lógica de la interfaz de usuario de las implementaciones concretas de cálculo y validación.
 */
interface IValidadorRespuesta {

    /**
     * Calcula el tiempo teórico a partir de la distancia y la velocidad (t = d / v).
     *
     * @param distancia distancia d recorrida en metros.
     * @param velocidad velocidad v constante en m/s.
     * @return tiempo t en segundos.
     */
    fun calcularTiempo(distancia: Double, velocidad: Double): Double

    /**
     * Calcula la posición final teórica x(t) usando los parámetros del problema.
     *
     * @param posicionInicial posición inicial x₀ en metros.
     * @param velocidadInicial velocidad inicial v₀ en m/s.
     * @param aceleracion aceleración a en m/s².
     * @param distancia distancia d en metros.
     * @param velocidad velocidad v en m/s para determinar el tiempo.
     * @return posición final teórica esperada.
     */
    fun calcularPosicionFinal(
        posicionInicial: Double,
        velocidadInicial: Double,
        aceleracion: Double,
        distancia: Double,
        velocidad: Double
    ): Double

    /**
     * Valida si la entrada proporcionada por el usuario coincide con el valor teórico esperado.
     *
     * @param entradaUsuario texto numérico ingresado por el jugador.
     * @param valorTeorico resultado exacto esperado.
     * @return true si la respuesta es correcta dentro de la tolerancia admitida.
     */
    fun validar(entradaUsuario: String, valorTeorico: Double): Boolean
}
