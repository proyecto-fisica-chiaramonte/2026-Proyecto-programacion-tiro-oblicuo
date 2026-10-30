package org.example

import com.example.juegofisica.ICalculadora
import com.example.juegofisica.IValidadorRespuesta
import com.example.juegofisica.MotorCalculadora
import com.example.juegofisica.TemporizadorPartida
import com.example.juegofisica.ValidadorRespuestaMRUV
import com.example.juegofisica.ValidadorTiempoMRUV
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppTest {

    @Test
    fun testCalculoTiempoMRUV() {
        val validador: IValidadorRespuesta = ValidadorRespuestaMRUV()
        // d = 60, v = 20 => t = d / v = 60 / 20 = 3
        val t = validador.calcularTiempo(distancia = 60.0, velocidad = 20.0)
        assertEquals(3.0, t, 0.0001)
    }

    @Test
    fun testCalculoPosicionFinalMRUV() {
        val validador: IValidadorRespuesta = ValidadorRespuestaMRUV()
        // d = 60, v = 20 => t = 3
        // x(t) = x0 + v0 * t + 1/2 * a * t^2
        // x(3) = 5 + 10 * 3 + 0.5 * 4 * 9 = 5 + 30 + 18 = 53
        val xFinal = validador.calcularPosicionFinal(
            posicionInicial = 5.0,
            velocidadInicial = 10.0,
            aceleracion = 4.0,
            distancia = 60.0,
            velocidad = 20.0
        )
        assertEquals(53.0, xFinal, 0.0001)
    }

    @Test
    fun testValidarRespuestaCorrectaConTolerancia() {
        val validador: IValidadorRespuesta = ValidadorRespuestaMRUV(tolerancia = 0.1)
        val valorTeorico = 53.0

        assertTrue(validador.validar("53", valorTeorico))
        assertTrue(validador.validar("53.0", valorTeorico))
        assertTrue(validador.validar("53,0", valorTeorico))
        assertTrue(validador.validar("53.05", valorTeorico))
        assertTrue(validador.validar(" 52.95 ", valorTeorico))
    }

    @Test
    fun testValidarRespuestaIncorrectaOInvalida() {
        val validador: IValidadorRespuesta = ValidadorRespuestaMRUV(tolerancia = 0.1)
        val valorTeorico = 53.0

        assertFalse(validador.validar("54", valorTeorico))
        assertFalse(validador.validar("52.8", valorTeorico))
        assertFalse(validador.validar("abc", valorTeorico))
        assertFalse(validador.validar("", valorTeorico))
    }

    @Test
    fun testMotorCalculadoraOperacionesBasicas() {
        val calc: ICalculadora = MotorCalculadora()

        // 12 + 8 = 20
        calc.ingresarDigito("1")
        calc.ingresarDigito("2")
        calc.establecerOperador("+")
        calc.ingresarDigito("8")
        val resultadoSuma = calc.calcularResultado()
        assertEquals("20", resultadoSuma)

        // 20 * 3 = 60
        calc.establecerOperador("*")
        calc.ingresarDigito("3")
        val resultadoMultiplicacion = calc.calcularResultado()
        assertEquals("60", resultadoMultiplicacion)

        // 60 - 15 = 45
        calc.establecerOperador("-")
        calc.ingresarDigito("1")
        calc.ingresarDigito("5")
        val resultadoResta = calc.calcularResultado()
        assertEquals("45", resultadoResta)

        // 45 / 9 = 5
        calc.establecerOperador("/")
        calc.ingresarDigito("9")
        val resultadoDivision = calc.calcularResultado()
        assertEquals("5", resultadoDivision)
    }

    @Test
    fun testMotorCalculadoraDivisionPorCero() {
        val calc: ICalculadora = MotorCalculadora()
        calc.ingresarDigito("7")
        calc.establecerOperador("/")
        calc.ingresarDigito("0")
        val resultado = calc.calcularResultado()
        assertEquals("Error", resultado)
    }

    @Test
    fun testMotorCalculadoraDecimalesYRaiz() {
        val calc: ICalculadora = MotorCalculadora()
        calc.ingresarDigito("3")
        calc.ingresarPunto()
        calc.ingresarDigito("5")
        assertEquals("3.5", calc.display)

        calc.limpiar()
        assertEquals("0", calc.display)

        // Raíz de 25 = 5
        calc.ingresarDigito("2")
        calc.ingresarDigito("5")
        val raiz = calc.aplicarRaiz()
        assertEquals("5", raiz)
    }

    @Test
    fun testTemporizadorFormateoYDecremento() {
        var tickSegundos = -1
        var finLlamado = false

        val temporizador = TemporizadorPartida(
            onTick = { s, _ -> tickSegundos = s },
            onFin = { finLlamado = true }
        )

        assertEquals("02:00", temporizador.formatearTiempo(120))
        assertEquals("00:05", temporizador.formatearTiempo(5))
        assertEquals("00:00", temporizador.formatearTiempo(0))

        // Configurar 2 segundos manualmente para probar decremento sin hilos de JavaFX
        temporizador.configurarTiempo(2)
        assertEquals(2, temporizador.tiempoRestanteSegundos)

        val sigue1 = temporizador.decrementarSegundo()
        assertTrue(sigue1)
        assertEquals(1, temporizador.tiempoRestanteSegundos)
        assertEquals(1, tickSegundos)
        assertEquals(1, temporizador.tiempoEmpleadoSegundos)
        assertFalse(finLlamado)

        val sigue2 = temporizador.decrementarSegundo()
        assertFalse(sigue2)
        assertEquals(0, temporizador.tiempoRestanteSegundos)
        assertEquals(2, temporizador.tiempoEmpleadoSegundos)
        assertTrue(finLlamado)
    }

    @Test
    fun testCalculoYValidacionTiempoModoFacil() {
        val validadorTiempo = ValidadorTiempoMRUV()

        // d = 16, a = 2 => t = sqrt(2 * 16 / 2) = 4.0
        assertEquals(4.0, validadorTiempo.calcularTiempo(16.0, 2.0), 0.0001)
        assertEquals(0.0, validadorTiempo.calcularTiempo(-10.0, 2.0))
        assertEquals(0.0, validadorTiempo.calcularTiempo(16.0, 0.0))

        assertTrue(validadorTiempo.validar("4", 4.0))
        assertTrue(validadorTiempo.validar("4,05", 4.0))
        assertFalse(validadorTiempo.validar("5", 4.0))
        assertFalse(validadorTiempo.validar("abc", 4.0))
    }
}
