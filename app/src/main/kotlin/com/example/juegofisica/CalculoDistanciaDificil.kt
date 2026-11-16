package com.example.juegofisica

/**
 * Estrategia concreta de cálculo para el Modo Difícil de MRUV (OCP / SRP).
 *
 * Genera problemas donde el jugador debe calcular primero t = d / v y luego la posición
 * final x(t) = x₀ + v₀ · t + ½ · a · t², delegando la validación en [IValidadorRespuesta].
 */
class CalculoDistanciaDificil(
    private val validador: IValidadorRespuesta = ValidadorRespuestaMRUV()
) : IEstrategiaCalculoMRUV {

    override val formulaPrincipal: String = "x(t) = x₀ + v₀ · t + ½ · a · t²"

    override val formulaSecundaria: String = "t = d / v"

    override val etiquetaIncognita: String = "X ="

    override var resultadoEsperado: Double = 0.0
        private set

    override fun generarNuevosDatos(): List<String> {
        val v = (5..30).random()
        val d = (v * listOf(2, 3, 4, 5, 6, 8, 10).random()).toDouble()
        val a = (1..10).random().toDouble()
        val v0 = (0..20).random().toDouble()
        val x0 = (0..50).random().toDouble()

        resultadoEsperado = validador.calcularPosicionFinal(doubleArrayOf(x0, v0, a), d, v.toDouble())
        return listOf("a = ${a.toInt()} m/s²", "d = ${d.toInt()} m", "v = $v m/s", "v₀ = ${v0.toInt()} m/s", "x₀ = ${x0.toInt()} m")
    }

    override fun validarRespuesta(entradaUsuario: String): Boolean {
        return validador.validar(entradaUsuario, resultadoEsperado)
    }
}
