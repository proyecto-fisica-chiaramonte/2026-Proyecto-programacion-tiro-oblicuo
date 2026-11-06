package org.example

import com.example.juegofisica.CalculadorTiroOblicuo
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

        assertEquals("01:00", temporizador.formatearTiempo(60))
        assertEquals(60, temporizador.tiempoInicialSegundos)
        assertEquals(60, temporizador.tiempoRestanteSegundos)
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

    @Test
    fun testCalculadorTiroOblicuo() {
        val calc = CalculadorTiroOblicuo()

        // Lanzamiento horizontal (0 grados): x(2) = 10 + 20 * 1 * 2 = 50
        assertEquals(50.0, calc.calcularX(x0 = 10.0, v0 = 20.0, anguloGrados = 0.0, t = 2.0), 0.0001)

        // Lanzamiento vertical (90 grados), y0 = 0, v0 = 20, g = 10: y(1) = 20*1 - 0.5*10*1 = 15
        assertEquals(15.0, calc.calcularY(y0 = 0.0, v0 = 20.0, anguloGrados = 90.0, gravedad = 10.0, t = 1.0), 0.0001)

        // Tiempo de vuelo con y0 = 0, v0 = 20, angulo = 90, g = 10 => t = 2*20/10 = 4.0s
        assertEquals(4.0, calc.calcularTiempoVuelo(y0 = 0.0, v0 = 20.0, anguloGrados = 90.0, gravedad = 10.0), 0.0001)

        // Altura máxima con y0 = 0, v0 = 20, angulo = 90, g = 10 => h = 400 / 20 = 20.0m
        assertEquals(20.0, calc.calcularAlturaMaxima(y0 = 0.0, v0 = 20.0, anguloGrados = 90.0, gravedad = 10.0), 0.0001)

        // Alcance horizontal clásico con y0 = 0, v0 = 20, angulo = 45, g = 10 => R = 400 * 1 / 10 = 40.0m
        assertEquals(40.0, calc.calcularAlcanceHorizontal(y0 = 0.0, v0 = 20.0, anguloGrados = 45.0, gravedad = 10.0), 0.0001)

        // Lanzamiento desde altura inicial y0 = 20m, horizontal (angulo = 0), v0 = 15, g = 10:
        // t_vuelo = sqrt(2*20/10) = 2.0s, y(1.0) = 20 - 0.5*10*1 = 15.0m
        assertEquals(2.0, calc.calcularTiempoVuelo(y0 = 20.0, v0 = 15.0, anguloGrados = 0.0, gravedad = 10.0), 0.0001)
        assertEquals(15.0, calc.calcularY(y0 = 20.0, v0 = 15.0, anguloGrados = 0.0, gravedad = 10.0, t = 1.0), 0.0001)

        // Verificar que con xf = 10m e yf = 10m desde (0, 0), la pelota sube al pico y = 10m y cae en x = 10m (y = 0m)
        val anguloPico = calc.calcularAnguloParaPicoYAlcance(xf = 10.0, yf = 10.0, anguloPorDefecto = 45.0)
        val v0Pico = calc.calcularVelocidadInicialRequerida(v0Ingresada = 0.0, xf = 10.0, yf = 10.0, anguloGrados = anguloPico, gravedad = 9.8)
        val tTotal = calc.calcularTiempoVuelo(y0 = 0.0, v0 = v0Pico, anguloGrados = anguloPico, gravedad = 9.8)

        // En la mitad del vuelo (t = tTotal / 2), pasa por el pico (x = 5m, y = 10m)
        assertEquals(5.0, calc.calcularX(0.0, v0Pico, anguloPico, tTotal * 0.5), 0.001)
        assertEquals(10.0, calc.calcularY(0.0, v0Pico, anguloPico, 9.8, tTotal * 0.5), 0.001)

        // Al finalizar el vuelo (t = tTotal), cae a tierra en (x = 10m, y = 0m)
        assertEquals(10.0, calc.calcularX(0.0, v0Pico, anguloPico, tTotal), 0.001)
        assertEquals(0.0, calc.calcularY(0.0, v0Pico, anguloPico, 9.8, tTotal), 0.001)
    }
}
