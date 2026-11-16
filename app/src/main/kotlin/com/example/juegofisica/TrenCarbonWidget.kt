package com.example.juegofisica

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import kotlin.math.cos
import kotlin.math.sin

/**
 * Componente visual dedicado exclusivamente al trazado geométrico de la locomotora clásica
 * de vapor a carbón y a la delegación de su sistema de partículas en [EfectoHumoTren] (SRP).
 */
class TrenCarbonWidget(
    private val esModoDificil: Boolean = true,
    private val efectoHumo: EfectoHumoTren = EfectoHumoTren()
) {

    /** Coordenada X local base de la locomotora durante el renderizado actual. */
    private var xBase: Double = -60.0

    /** Coordenada Y local base de la locomotora durante el renderizado actual. */
    private var yBase: Double = -24.0

    /**
     * Actualiza la emisión continua de humo y chispas delegando en [EfectoHumoTren].
     */
    fun actualizarEfectos(delta: Double, posicionTren: DoubleArray, estado: Int) {
        xBase = -60.0
        efectoHumo.actualizar(delta, posicionTren, estado)
    }

    /**
     * Dibuja la locomotora clásica de vapor a carbón y sus ruedas animadas.
     */
    fun dibujarLocomotora(gc: GraphicsContext, tiempoTotal: Double) {
        dibujarHazLuzYTender(gc)
        dibujarCabinaYCaldera(gc)
        dibujarChimeneaYFrente(gc)
        dibujarRuedasYBielas(gc, tiempoTotal)
    }

    /**
     * Dibuja las partículas de humo y chispas delegando en [EfectoHumoTren].
     */
    fun dibujarParticulas(gc: GraphicsContext) {
        yBase = -24.0
        efectoHumo.dibujar(gc)
    }

    private fun dibujarHazLuzYTender(gc: GraphicsContext) {
        gc.fill = RadialGradient(
            0.0, 0.0, xBase + 124.0, yBase + 14.0, 145.0, false, CycleMethod.NO_CYCLE,
            Stop(0.0, Color.rgb(255, 230, 120, 0.65)), Stop(1.0, Color.rgb(255, 180, 50, 0.0))
        )
        gc.fillPolygon(doubleArrayOf(xBase + 124.0, xBase + 270.0, xBase + 270.0), doubleArrayOf(yBase + 14.0, yBase - 22.0, yBase + 58.0), 3)
        gc.fill = if (esModoDificil) Color.web("#0D0D0F") else Color.web("#1C1C1E")
        gc.fillPolygon(doubleArrayOf(xBase + 2.0, xBase + 10.0, xBase + 25.0, xBase + 34.0), doubleArrayOf(yBase + 16.0, yBase + 6.0, yBase + 6.0, yBase + 16.0), 4)
        gc.fill = Color.web("#1A1C20")
        gc.fillRoundRect(xBase, yBase + 14.0, 35.0, 24.0, 4.0, 4.0)
    }

    private fun dibujarCabinaYCaldera(gc: GraphicsContext) {
        gc.fill = Color.web("#16181C")
        gc.fillRect(xBase + 39.0, yBase + 2.0, 28.0, 36.0)
        gc.fill = Color.web("#FFB300")
        gc.fillRoundRect(xBase + 45.0, yBase + 8.0, 15.0, 13.0, 6.0, 6.0)
        gc.fill = Color.web("#111317")
        gc.fillRoundRect(xBase + 66.0, yBase + 10.0, 54.0, 28.0, 8.0, 8.0)
        gc.stroke = Color.web("#C59B27")
        gc.lineWidth = 1.8
        gc.strokeLine(xBase + 76.0, yBase + 10.0, xBase + 76.0, yBase + 38.0)
        gc.strokeLine(xBase + 92.0, yBase + 10.0, xBase + 92.0, yBase + 38.0)
        gc.strokeLine(xBase + 106.0, yBase + 10.0, xBase + 106.0, yBase + 38.0)
    }

    private fun dibujarChimeneaYFrente(gc: GraphicsContext) {
        gc.fill = Color.web("#14161A")
        gc.fillRect(xBase + 104.0, yBase - 14.0, 10.0, 25.0)
        gc.fill = Color.web("#FFF59D")
        gc.fillOval(xBase + 121.0, yBase + 10.0, 6.0, 7.0)
        gc.fill = Color.web("#7F1D1D")
        gc.fillPolygon(doubleArrayOf(xBase + 116.0, xBase + 132.0, xBase + 116.0), doubleArrayOf(yBase + 28.0, yBase + 42.0, yBase + 42.0), 3)
        gc.fill = Color.web("#6D1B1B")
        gc.fillRect(xBase + 2.0, yBase + 37.0, 116.0, 4.0)
    }

    private fun dibujarRuedasYBielas(gc: GraphicsContext, tiempoTotal: Double) {
        val angulo = tiempoTotal * 18.0
        gc.fill = Color.web("#141414"); gc.stroke = Color.web("#90A4AE"); gc.lineWidth = 1.6
        for (i in 0..1) {
            gc.fillOval(xBase + 4.0 + i * 16.0, yBase + 39.0, 12.0, 12.0)
            gc.strokeOval(xBase + 4.0 + i * 16.0, yBase + 39.0, 12.0, 12.0)
        }
        for (i in 0..2) {
            gc.fillOval(xBase + 44.0 + i * 22.0, yBase + 36.0, 17.0, 17.0)
            gc.strokeOval(xBase + 44.0 + i * 22.0, yBase + 36.0, 17.0, 17.0)
        }
        gc.strokeLine(xBase + 52.5 + cos(angulo) * 4.5, yBase + 44.5 + sin(angulo) * 3.5, xBase + 108.0, yBase + 41.5)
    }
}
