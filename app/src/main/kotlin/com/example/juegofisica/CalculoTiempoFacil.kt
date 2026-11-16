package com.example.juegofisica

/**
 * Estrategia concreta de cálculo para el Modo Fácil de MRUV (OCP / SRP).
 *
 * Genera problemas donde el jugador debe despejar el tiempo t = √(2 · d / a) con valores
 * enteros exactos y delega la validación numérica en [ValidadorTiempoMRUV].
 *
 * @property validador componente encargado de evaluar t y verificar la tolerancia.
 */
class CalculoTiempoFacil(
    private val validador: ValidadorTiempoMRUV = ValidadorTiempoMRUV()
) : IEstrategiaCalculoMRUV {

    override val formulaPrincipal: String = "t = √(2 · d / a)"

    override val formulaSecundaria: String = "x(t) = x₀ + v₀ · t + ½ · a · t²"

    override val etiquetaIncognita: String = "t ="

    override var resultadoEsperado: Double = 0.0
        private set

    override fun generarNuevosDatos(): List<String> {
        val tiempoExacto = listOf(2, 3, 4, 5, 6).random()
        val opcionesA = listOf(1, 2, 4).filter { a -> (a * tiempoExacto * tiempoExacto) % 2 == 0 }
        val aceleracion = (if (opcionesA.isNotEmpty()) opcionesA.random() else 2).toDouble()
        val distancia = (aceleracion.toInt() * tiempoExacto * tiempoExacto / 2).toDouble()
        val velocidadInicial = (0..15).random().toDouble()
        val posicionInicial = (0..20).random().toDouble()

        resultadoEsperado = validador.calcularTiempo(distancia, aceleracion)

        return listOf(
            "a = ${aceleracion.toInt()} m/s²",
            "d = ${distancia.toInt()} m",
            "v₀ = ${velocidadInicial.toInt()} m/s",
            "x₀ = ${posicionInicial.toInt()} m"
        )
    }

    override fun validarRespuesta(entradaUsuario: String): Boolean {
        return validador.validar(entradaUsuario, resultadoEsperado)
    }
}
