package com.example.juegofisica

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlin.random.Random

/**
 * Sistema de partículas dedicado a emitir y animar las columnas de humo de carbón y las chispas
 * sobre los rieles para la locomotora clásica de vapor (Principio de Responsabilidad Única - SRP).
 */
class EfectoHumoTren {

    /** Coordenadas X de las nubes de humo de carbón y vapor. */
    private val humoX = mutableListOf<Double>()

    /** Coordenadas Y de las nubes de humo de carbón y vapor. */
    private val humoY = mutableListOf<Double>()

    /** Radios actuales de cada nube de humo. */
    private val humoRadio = mutableListOf<Double>()

    /** Opacidad restante (0.0 a 1.0) de cada nube de humo. */
    private val humoOpacidad = mutableListOf<Double>()

    /** Tono de gris/negro (0 = hollín negro, 1 = gris oscuro, 2 = vapor gris claro) de cada nube. */
    private val humoTono = mutableListOf<Int>()

    /** Coordenadas X de partículas de chispas en las ruedas. */
    private val chispasX = mutableListOf<Double>()

    /** Coordenadas Y de partículas de chispas en las ruedas. */
    private val chispasY = mutableListOf<Double>()

    /** Velocidades horizontales de las chispas. */
    private val chispasVx = mutableListOf<Double>()

    /** Velocidades verticales de las chispas. */
    private val chispasVy = mutableListOf<Double>()

    /** Vida restante (0.0 a 1.0) de las chispas. */
    private val chispasVida = mutableListOf<Double>()

    /**
     * Actualiza la emisión continua y el movimiento de humo y chispas según la posición del tren.
     */
    fun actualizar(delta: Double, xTren: Double, yTren: Double, estado: Int) {
        val xBocaChimenea = xTren + 111.0
        val yBocaChimenea = yTren - 46.0

        val cantidadHumo = (estado + 2).coerceIn(2, 4)
        emitirHumoChimenea(xBocaChimenea, yBocaChimenea, cantidadHumo)

        if (estado == 0 && Random.nextDouble() < 0.4) {
            emitirChispasRuedas(xTren + 90.0, yTren - 2.0, 2)
        } else if (estado == 1) {
            emitirChispasRuedas(xTren + 95.0, yTren - 2.0, 4)
        } else if (estado == 2) {
            emitirChispasRuedas(xTren + 105.0, yTren - 4.0, 7)
        }

        val velocidadVientoX = if (estado == 2) 115.0 else 82.0
        avanzarHumo(delta, velocidadVientoX)
        avanzarChispas(delta)
    }

    /**
     * Renderiza las nubes de humo negro/gris y las chispas metálicas sobre el lienzo.
     */
    fun dibujar(gc: GraphicsContext) {
        for (j in humoOpacidad.indices) {
            val r = humoRadio[j]
            val opacidad = humoOpacidad[j].coerceIn(0.0, 1.0)
            gc.fill = when (humoTono[j]) {
                0 -> Color.rgb(22, 22, 25, opacidad * 0.78)
                1 -> Color.rgb(58, 60, 64, opacidad * 0.68)
                else -> Color.rgb(120, 124, 130, opacidad * 0.55)
            }
            gc.fillOval(humoX[j] - r, humoY[j] - r, r * 2.0, r * 2.0)
        }

        for (i in chispasVida.indices) {
            val vida = chispasVida[i].coerceIn(0.0, 1.0)
            gc.fill = Color.rgb(255, (165 * vida).toInt() + 75, 20, vida)
            gc.fillOval(chispasX[i], chispasY[i], 3.0, 3.0)
        }
    }

    private fun emitirHumoChimenea(x: Double, y: Double, cantidad: Int) {
        for (i in 0 until cantidad) {
            humoX.add(x + Random.nextDouble(-3.0, 3.0))
            humoY.add(y + Random.nextDouble(-2.0, 2.0))
            humoRadio.add(Random.nextDouble(5.0, 10.0))
            humoOpacidad.add(0.85)
            humoTono.add(Random.nextInt(0, 3))
        }
    }

    private fun emitirChispasRuedas(x: Double, y: Double, cantidad: Int) {
        for (i in 0 until cantidad) {
            chispasX.add(x + Random.nextDouble(-4.0, 4.0))
            chispasY.add(y + Random.nextDouble(-2.0, 2.0))
            chispasVx.add(Random.nextDouble(-125.0, -35.0))
            chispasVy.add(Random.nextDouble(-85.0, -10.0))
            chispasVida.add(1.0)
        }
    }

    private fun avanzarHumo(delta: Double, velocidadVientoX: Double) {
        var j = humoOpacidad.size - 1
        while (j >= 0) {
            humoY[j] -= 32.0 * delta
            humoX[j] -= velocidadVientoX * delta
            humoRadio[j] += 14.0 * delta
            humoOpacidad[j] -= delta * 0.62
            if (humoOpacidad[j] <= 0.0) {
                humoX.removeAt(j)
                humoY.removeAt(j)
                humoRadio.removeAt(j)
                humoOpacidad.removeAt(j)
                humoTono.removeAt(j)
            }
            j--
        }
    }

    private fun avanzarChispas(delta: Double) {
        var i = chispasVida.size - 1
        while (i >= 0) {
            chispasX[i] += chispasVx[i] * delta
            chispasY[i] += chispasVy[i] * delta
            chispasVy[i] += 180.0 * delta
            chispasVida[i] -= delta * 2.4
            if (chispasVida[i] <= 0.0) {
                chispasX.removeAt(i)
                chispasY.removeAt(i)
                chispasVx.removeAt(i)
                chispasVy.removeAt(i)
                chispasVida.removeAt(i)
            }
            i--
        }
    }
}
