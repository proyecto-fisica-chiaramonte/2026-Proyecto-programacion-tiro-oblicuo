package org.example

import com.example.juegofisica.CalculadorTiroOblicuo
import com.example.juegofisica.CalculoDistanciaDificil
import com.example.juegofisica.CalculoTiempoFacil
import com.example.juegofisica.ICalculadora
import com.example.juegofisica.IEstrategiaCalculoMRUV
import com.example.juegofisica.IValidadorRespuesta
import com.example.juegofisica.MotorCalculadora
import com.example.juegofisica.ParametrosTiroOblicuo
import com.example.juegofisica.TemporizadorPartida
import com.example.juegofisica.ValidadorRespuestaMRUV
import com.example.juegofisica.ValidadorTiempoMRUV
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppTest {

    @Test
    fun testCalculoTiempoYPosicionFinalMRUV() {
        val validador: IValidadorRespuesta = ValidadorRespuestaMRUV()
        val t = validador.calcularTiempo(distancia = 60.0, velocidad = 20.0)
        assertEquals(3.0, t, 0.0001)
        val xFinal = validador.calcularPosicionFinal(doubleArrayOf(5.0, 10.0, 4.0), distancia = 60.0, velocidad = 20.0)
        assertEquals(53.0, xFinal, 0.0001)
    }

    @Test
    fun testValidarRespuestaConTolerancia() {
        val validador: IValidadorRespuesta = ValidadorRespuestaMRUV(tolerancia = 0.1)
        assertTrue(validador.validar("53", 53.0))
        assertTrue(validador.validar("53,05", 53.0))
        assertTrue(validador.validar(" 52.95 ", 53.0))
        assertFalse(validador.validar("54", 53.0))
        assertFalse(validador.validar("abc", 53.0))
        assertFalse(validador.validar("", 53.0))
    }

    @Test
    fun testMotorCalculadoraOperacionesBasicas() {
        val calc: ICalculadora = MotorCalculadora()
        calc.ingresarDigito("1")
        calc.ingresarDigito("2")
        calc.establecerOperador("+")
        calc.ingresarDigito("8")
        assertEquals("20", calc.calcularResultado())
        calc.establecerOperador("*")
        calc.ingresarDigito("3")
        assertEquals("60", calc.calcularResultado())
        calc.establecerOperador("/")
        calc.ingresarDigito("4")
        assertEquals("15", calc.calcularResultado())
    }

    @Test
    fun testMotorCalculadoraDivisionPorCeroYRaiz() {
        val calc: ICalculadora = MotorCalculadora()
        calc.ingresarDigito("7")
        calc.establecerOperador("/")
        calc.ingresarDigito("0")
        assertEquals("Error", calc.calcularResultado())
        calc.limpiar()
        calc.ingresarDigito("2")
        calc.ingresarDigito("5")
        assertEquals("5", calc.aplicarRaiz())
    }

    @Test
    fun testTemporizadorFormateoYDecremento() {
        var finLlamado = false
        val temporizador = TemporizadorPartida(onFin = { finLlamado = true })
        assertEquals("01:00", temporizador.formatearTiempo(60))
        assertEquals(60, temporizador.obtenerTiempoInicial())
        temporizador.configurarTiempo(2)
        assertTrue(temporizador.decrementarSegundo())
        assertEquals(1, temporizador.obtenerTiempoRestante())
        assertEquals(1, temporizador.obtenerTiempoEmpleado())
        assertFalse(temporizador.decrementarSegundo())
        assertTrue(finLlamado)
    }

    @Test
    fun testCalculoYValidacionTiempoModoFacil() {
        val validadorTiempo = ValidadorTiempoMRUV()
        assertEquals(4.0, validadorTiempo.calcularTiempo(16.0, 2.0), 0.0001)
        assertEquals(0.0, validadorTiempo.calcularTiempo(-10.0, 2.0))
        assertTrue(validadorTiempo.validar("4,05", 4.0))
        assertFalse(validadorTiempo.validar("5", 4.0))
    }

    @Test
    fun testCalculadorTiroOblicuoCinematica() {
        val calc = CalculadorTiroOblicuo(10.0)
        assertEquals(40.0, calc.calcularX(v0 = 20.0, anguloGrados = 0.0, t = 2.0), 0.0001)
        assertEquals(15.0, calc.calcularY(v0 = 20.0, anguloGrados = 90.0, t = 1.0), 0.0001)
        assertEquals(4.0, calc.calcularTiempoVuelo(v0 = 20.0, anguloGrados = 90.0), 0.0001)
        assertEquals(20.0, calc.calcularAlturaMaxima(v0 = 20.0, anguloGrados = 90.0), 0.0001)
        assertEquals(40.0, calc.calcularAlcanceHorizontal(v0 = 20.0, anguloGrados = 45.0), 0.0001)
    }

    @Test
    fun testTiroOblicuoPicoYVelocidad() {
        val calc = CalculadorTiroOblicuo(9.8)
        val anguloPico = calc.calcularAnguloParaPicoYAlcance(xf = 10.0, yf = 10.0, anguloPorDefecto = 45.0)
        val params = ParametrosTiroOblicuo(0.0, 9.8, anguloPico).configurarObjetivo(10.0, 10.0)
        val v0Pico = calc.calcularVelocidadInicialRequerida(params)
        val tTotal = calc.calcularTiempoVuelo(v0 = v0Pico, anguloGrados = anguloPico)
        assertEquals(10.0, calc.calcularY(v0Pico, anguloPico, tTotal * 0.5), 0.001)
        assertEquals(10.0, calc.calcularX(v0Pico, anguloPico, tTotal), 0.001)
        assertTrue(calc.calcularDuracionVisualSegundos(10.0) > calc.calcularDuracionVisualSegundos(100.0) * 9.0)
    }

    @Test
    fun testEstrategiasCalculoMRUVPolimorfismoOCP() {
        val facil: IEstrategiaCalculoMRUV = CalculoTiempoFacil()
        assertEquals(4, facil.generarNuevosDatos().size)
        assertTrue(facil.validarRespuesta(facil.resultadoEsperado.toString()))
        val dificil: IEstrategiaCalculoMRUV = CalculoDistanciaDificil()
        assertEquals(5, dificil.generarNuevosDatos().size)
        assertTrue(dificil.validarRespuesta(dificil.resultadoEsperado.toString()))
    }
}
