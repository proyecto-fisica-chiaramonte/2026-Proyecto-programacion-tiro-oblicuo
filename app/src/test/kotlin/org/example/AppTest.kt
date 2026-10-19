package org.example

import com.example.juegofisica.ValidadorRespuestaMRUV
import com.example.juegofisica.ValidadorTiempoMRUV
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppTest {

    @Test
    fun testCalculoTiempoMRUV() {
        val validador = ValidadorRespuestaMRUV()
        // d = 60, v = 20 => t = d / v = 60 / 20 = 3
        val t = validador.calcularTiempo(distancia = 60.0, velocidad = 20.0)
        assertEquals(3.0, t, 0.0001)
    }

    @Test
    fun testCalculoPosicionFinalMRUV() {
        val validador = ValidadorRespuestaMRUV()
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
        val validador = ValidadorRespuestaMRUV(tolerancia = 0.1)
        val valorTeorico = 53.0

        assertTrue(validador.validar("53", valorTeorico))
        assertTrue(validador.validar("53.0", valorTeorico))
        assertTrue(validador.validar("53,0", valorTeorico))
        assertTrue(validador.validar("53.05", valorTeorico))
        assertTrue(validador.validar(" 52.95 ", valorTeorico))
    }

    @Test
    fun testValidarRespuestaIncorrectaOInvalida() {
        val validador = ValidadorRespuestaMRUV(tolerancia = 0.1)
        val valorTeorico = 53.0

        assertFalse(validador.validar("54", valorTeorico))
        assertFalse(validador.validar("52.8", valorTeorico))
        assertFalse(validador.validar("abc", valorTeorico))
        assertFalse(validador.validar("", valorTeorico))
    }

    @Test
    fun testCalculoTiempoModoFacil() {
        val validadorTiempo = ValidadorTiempoMRUV()

        // d = 16, a = 2 => t = sqrt(2 * 16 / 2) = sqrt(16) = 4.0
        val t1 = validadorTiempo.calcularTiempo(distancia = 16.0, aceleracion = 2.0)
        assertEquals(4.0, t1, 0.0001)

        // d = 50, a = 4 => t = sqrt(2 * 50 / 4) = sqrt(25) = 5.0
        val t2 = validadorTiempo.calcularTiempo(distancia = 50.0, aceleracion = 4.0)
        assertEquals(5.0, t2, 0.0001)

        // Parámetros inválidos
        assertEquals(0.0, validadorTiempo.calcularTiempo(distancia = -10.0, aceleracion = 2.0))
        assertEquals(0.0, validadorTiempo.calcularTiempo(distancia = 16.0, aceleracion = 0.0))
        assertEquals(0.0, validadorTiempo.calcularTiempo(distancia = 16.0, aceleracion = -2.0))
    }

    @Test
    fun testValidarTiempoModoFacilConTolerancia() {
        val validadorTiempo = ValidadorTiempoMRUV(tolerancia = 0.1)
        val valorTeorico = 4.0

        assertTrue(validadorTiempo.validar("4", valorTeorico))
        assertTrue(validadorTiempo.validar("4.0", valorTeorico))
        assertTrue(validadorTiempo.validar("4,0", valorTeorico))
        assertTrue(validadorTiempo.validar("4.08", valorTeorico))
        assertTrue(validadorTiempo.validar("3.92", valorTeorico))

        assertFalse(validadorTiempo.validar("5", valorTeorico))
        assertFalse(validadorTiempo.validar("3.8", valorTeorico))
        assertFalse(validadorTiempo.validar("4.2", valorTeorico))
        assertFalse(validadorTiempo.validar("abc", valorTeorico))
        assertFalse(validadorTiempo.validar("", valorTeorico))
    }
}
