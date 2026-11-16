package com.example.juegofisica

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlin.random.Random

/**
 * Sistema de partículas dedicado a emitir y animar las columnas de humo de carbón y las chispas
 * sobre los rieles para la locomotora clásica de vapor (Principio de Responsabilidad Única - SRP).
 */
class EfectoHumoTren {

    /** Lista de partículas de humo: cada elemento es [x, y, radio, opacidad, tono]. */
    private val particulasHumo = mutableListOf<DoubleArray>()

    /** Lista de partículas de chispas: cada elemento es [x, y, vx, vy, vida]. */
    private val particulasChispas = mutableListOf<DoubleArray>()

    /**
     * Actualiza la emisión continua y el movimiento de humo y chispas según la posición del tren.
     */
    fun actualizar(delta: Double, posicionTren: DoubleArray, estado: Int) {
        val xTren = posicionTren[0]
        val yTren = posicionTren[1]
        emitirHumoChimenea(xTren + 111.0, yTren - 46.0, (estado + 2).coerceIn(2, 4))
        if (estado > 0 || Random.nextDouble() < 0.4) {
            emitirChispasRuedas(xTren + 95.0, yTren - 2.0, if (estado == 2) 6 else 2)
        }
        avanzarParticulas(delta, if (estado == 2) 115.0 else 82.0)
    }

    /**
     * Renderiza las nubes de humo negro/gris y las chispas metálicas sobre el lienzo.
     */
    fun dibujar(gc: GraphicsContext) {
        for (h in particulasHumo) {
            val r = h[2]
            val op = h[3].coerceIn(0.0, 1.0)
            gc.fill = if (h[4] < 0.5) Color.rgb(22, 22, 25, op * 0.78) else Color.rgb(80, 84, 90, op * 0.62)
            gc.fillOval(h[0] - r, h[1] - r, r * 2.0, r * 2.0)
        }
        for (c in particulasChispas) {
            val vida = c[4].coerceIn(0.0, 1.0)
            gc.fill = Color.rgb(255, (165 * vida).toInt() + 75, 20, vida)
            gc.fillOval(c[0], c[1], 3.0, 3.0)
        }
    }

    private fun emitirHumoChimenea(x: Double, y: Double, cantidad: Int) {
        for (i in 0 until cantidad) {
            particulasHumo.add(
                doubleArrayOf(
                    x + Random.nextDouble(-3.0, 3.0),
                    y + Random.nextDouble(-2.0, 2.0),
                    Random.nextDouble(5.0, 10.0),
                    0.85,
                    Random.nextInt(0, 2).toDouble()
                )
            )
        }
    }

    private fun emitirChispasRuedas(x: Double, y: Double, cantidad: Int) {
        for (i in 0 until cantidad) {
            particulasChispas.add(
                doubleArrayOf(
                    x + Random.nextDouble(-4.0, 4.0),
                    y + Random.nextDouble(-2.0, 2.0),
                    Random.nextDouble(-125.0, -35.0),
                    Random.nextDouble(-85.0, -10.0),
                    1.0
                )
            )
        }
    }

    private fun avanzarParticulas(delta: Double, velocidadVientoX: Double) {
        particulasHumo.forEach { h ->
            h[0] -= velocidadVientoX * delta; h[1] -= 32.0 * delta; h[2] += 14.0 * delta; h[3] -= delta * 0.62
        }
        particulasHumo.removeAll { it[3] <= 0.0 }
        particulasChispas.forEach { c ->
            c[0] += c[2] * delta; c[1] += c[3] * delta; c[3] += 180.0 * delta; c[4] -= delta * 2.4
        }
        particulasChispas.removeAll { it[4] <= 0.0 }
    }
}
