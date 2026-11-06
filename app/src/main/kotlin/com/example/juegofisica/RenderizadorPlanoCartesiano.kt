package com.example.juegofisica

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

/**
 * Renderizador dedicado a dibujar el plano cartesiano con fondo negro (`Color.BLACK`),
 * eje X azul (`Color.BLUE`), eje Y rojo (`Color.RED`), grilla de 10m en 10m, estela punteada
 * blanca y pelota blanca (`Color.WHITE`) para la simulación de Tiro Oblicuo (SRP).
 */
class RenderizadorPlanoCartesiano {

    /**
     * Dibuja la grilla de 10m en 10m y ambos ejes cartesianos graduados.
     */
    fun dibujarEjesYGrilla10m(
        gc: GraphicsContext,
        origenX: Double,
        origenY: Double,
        anchoUtil: Double,
        altoUtil: Double,
        maxEscalaX: Double,
        maxEscalaY: Double,
        pixelesPorMetroX: Double,
        pixelesPorMetroY: Double
    ) {
        gc.font = Font.font("System", FontWeight.BOLD, 9.5)
        val limiteMetrosX = maxEscalaX.toInt()
        val limiteMetrosY = maxEscalaY.toInt()

        dibujarLineasGrilla(gc, origenX, origenY, anchoUtil, altoUtil, limiteMetrosX, limiteMetrosY, pixelesPorMetroX, pixelesPorMetroY)
        dibujarEjeXAzul(gc, origenX, origenY, anchoUtil, limiteMetrosX, pixelesPorMetroX)
        dibujarEjeYRojo(gc, origenX, origenY, altoUtil, limiteMetrosY, pixelesPorMetroY)
    }

    /**
     * Dibuja la estela blanca punteada y la pelota blanca en su coordenada actual.
     */
    fun dibujarTrayectoriaYPelota(
        gc: GraphicsContext,
        origenX: Double,
        origenY: Double,
        pixelesPorMetroX: Double,
        pixelesPorMetroY: Double,
        estelaX: List<Double>,
        estelaY: List<Double>,
        pelotaX: Double,
        pelotaY: Double
    ) {
        if (estelaX.size > 1) {
            gc.save()
            gc.stroke = Color.WHITE
            gc.lineWidth = 2.0
            gc.setLineDashes(5.0, 4.0)
            for (i in 0 until estelaX.size - 1) {
                gc.strokeLine(
                    origenX + estelaX[i] * pixelesPorMetroX,
                    origenY - estelaY[i] * pixelesPorMetroY,
                    origenX + estelaX[i + 1] * pixelesPorMetroX,
                    origenY - estelaY[i + 1] * pixelesPorMetroY
                )
            }
            gc.restore()
        }

        val radioPelota = 6.0
        val px = origenX + pelotaX * pixelesPorMetroX
        val py = origenY - pelotaY * pixelesPorMetroY
        gc.fill = Color.WHITE
        gc.fillOval(px - radioPelota, py - radioPelota, radioPelota * 2.0, radioPelota * 2.0)
    }

    private fun dibujarLineasGrilla(
        gc: GraphicsContext,
        origenX: Double,
        origenY: Double,
        anchoUtil: Double,
        altoUtil: Double,
        limiteMetrosX: Int,
        limiteMetrosY: Int,
        pixelesPorMetroX: Double,
        pixelesPorMetroY: Double
    ) {
        gc.stroke = Color.web("#181818")
        gc.lineWidth = 1.0
        var mGridX = 10
        while (mGridX <= limiteMetrosX) {
            val px = origenX + mGridX * pixelesPorMetroX
            gc.strokeLine(px, origenY, px, origenY - altoUtil)
            mGridX += 10
        }
        var mGridY = 10
        while (mGridY <= limiteMetrosY) {
            val py = origenY - mGridY * pixelesPorMetroY
            gc.strokeLine(origenX, py, origenX + anchoUtil, py)
            mGridY += 10
        }
    }

    private fun dibujarEjeXAzul(
        gc: GraphicsContext,
        origenX: Double,
        origenY: Double,
        anchoUtil: Double,
        limiteMetrosX: Int,
        pixelesPorMetroX: Double
    ) {
        gc.stroke = Color.BLUE
        gc.lineWidth = 2.5
        gc.strokeLine(origenX, origenY, origenX + anchoUtil, origenY)

        var ultimoTextoX = -100.0
        var mX = 10
        while (mX <= limiteMetrosX) {
            val px = origenX + mX * pixelesPorMetroX
            gc.stroke = Color.BLUE
            gc.lineWidth = 1.6
            gc.strokeLine(px, origenY - 4.0, px, origenY + 4.0)
            if (px - ultimoTextoX >= 25.0) {
                gc.fill = Color.LIGHTBLUE
                gc.fillText("${mX}m", px - 11.0, origenY + 16.0)
                ultimoTextoX = px
            }
            mX += 10
        }
        gc.fill = Color.BLUE
        gc.fillText("X (m)", origenX + anchoUtil - 26.0, origenY + 31.0)
    }

    private fun dibujarEjeYRojo(
        gc: GraphicsContext,
        origenX: Double,
        origenY: Double,
        altoUtil: Double,
        limiteMetrosY: Int,
        pixelesPorMetroY: Double
    ) {
        gc.stroke = Color.RED
        gc.lineWidth = 2.5
        gc.strokeLine(origenX, origenY, origenX, origenY - altoUtil)

        var ultimoTextoY = origenY + 100.0
        var mY = 10
        while (mY <= limiteMetrosY) {
            val py = origenY - mY * pixelesPorMetroY
            gc.stroke = Color.RED
            gc.lineWidth = 1.6
            gc.strokeLine(origenX - 4.0, py, origenX + 4.0, py)
            if (ultimoTextoY - py >= 13.0) {
                gc.fill = Color.SALMON
                gc.fillText("${mY}m", 4.0, py + 3.5)
                ultimoTextoY = py
            }
            mY += 10
        }
        gc.fill = Color.RED
        gc.fillText("Y (m)", 8.0, origenY - altoUtil - 6.0)
    }
}
