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
 *
 * @property esModoDificil indica si se aplica el matiz de brasas intensas del modo difícil.
 * @property efectoHumo colaborador encargado de las nubes de humo y chispas sobre los rieles.
 */
class TrenCarbonWidget(
    private val esModoDificil: Boolean = true,
    private val efectoHumo: EfectoHumoTren = EfectoHumoTren()
) {

    /**
     * Actualiza la emisión continua de humo y chispas delegando en [EfectoHumoTren].
     */
    fun actualizarEfectos(delta: Double, xTren: Double, yTren: Double, estado: Int) {
        efectoHumo.actualizar(delta, xTren, yTren, estado)
    }

    /**
     * Dibuja la locomotora clásica de vapor a carbón dividida en métodos cohesivos cortos.
     */
    fun dibujarLocomotora(gc: GraphicsContext, x: Double, y: Double, tiempoTotal: Double) {
        dibujarHazLuzFrontal(gc, x, y)
        dibujarVagonTender(gc, x, y)
        dibujarCabinaYCaldera(gc, x, y)
        dibujarChimeneaYFrente(gc, x, y)
        dibujarRuedasYBielas(gc, x, y, tiempoTotal)
    }

    /**
     * Dibuja las partículas de humo y chispas delegando en [EfectoHumoTren].
     */
    fun dibujarParticulas(gc: GraphicsContext) {
        efectoHumo.dibujar(gc)
    }

    private fun dibujarHazLuzFrontal(gc: GraphicsContext, x: Double, y: Double) {
        gc.fill = RadialGradient(
            0.0, 0.0, x + 124.0, y + 14.0, 145.0, false, CycleMethod.NO_CYCLE,
            Stop(0.0, Color.rgb(255, 230, 120, 0.65)),
            Stop(0.5, Color.rgb(255, 200, 80, 0.25)),
            Stop(1.0, Color.rgb(255, 180, 50, 0.0))
        )
        gc.fillPolygon(
            doubleArrayOf(x + 124.0, x + 270.0, x + 270.0),
            doubleArrayOf(y + 14.0, y - 22.0, y + 58.0),
            3
        )
    }

    private fun dibujarVagonTender(gc: GraphicsContext, x: Double, y: Double) {
        gc.fill = Color.web("#0D0D0F")
        gc.fillPolygon(
            doubleArrayOf(x + 2.0, x + 8.0, x + 18.0, x + 28.0, x + 34.0),
            doubleArrayOf(y + 16.0, y + 7.0, y + 5.0, y + 8.0, y + 16.0),
            5
        )
        if (esModoDificil) {
            gc.fill = Color.web("#FF5722")
            gc.fillOval(x + 12.0, y + 9.0, 5.0, 3.0)
            gc.fillOval(x + 21.0, y + 10.0, 4.0, 3.0)
        }
        gc.fill = Color.web("#1A1C20")
        gc.fillRoundRect(x, y + 14.0, 35.0, 24.0, 4.0, 4.0)
        gc.stroke = Color.web("#8D6E63")
        gc.lineWidth = 1.2
        gc.strokeRoundRect(x, y + 14.0, 35.0, 24.0, 4.0, 4.0)
        gc.fill = Color.web("#333333")
        gc.fillRect(x + 34.0, y + 28.0, 6.0, 5.0)
    }

    private fun dibujarCabinaYCaldera(gc: GraphicsContext, x: Double, y: Double) {
        gc.fill = Color.web("#16181C")
        gc.fillRect(x + 39.0, y + 2.0, 28.0, 36.0)
        gc.fill = Color.web("#2C3038")
        gc.fillRoundRect(x + 36.0, y - 1.0, 33.0, 5.0, 4.0, 4.0)
        gc.fill = Color.web("#FFB300")
        gc.fillRoundRect(x + 45.0, y + 8.0, 15.0, 13.0, 6.0, 6.0)

        gc.fill = Color.web("#111317")
        gc.fillRoundRect(x + 66.0, y + 10.0, 54.0, 28.0, 8.0, 8.0)
        gc.fill = Color.web("#282C34")
        gc.fillRect(x + 67.0, y + 13.0, 51.0, 5.0)

        gc.stroke = Color.web("#C59B27")
        gc.lineWidth = 1.8
        gc.strokeLine(x + 76.0, y + 10.0, x + 76.0, y + 38.0)
        gc.strokeLine(x + 92.0, y + 10.0, x + 92.0, y + 38.0)
        gc.strokeLine(x + 106.0, y + 10.0, x + 106.0, y + 38.0)

        gc.fill = Color.web("#1D2026")
        gc.fillArc(x + 79.0, y + 2.0, 12.0, 16.0, 0.0, 180.0, javafx.scene.shape.ArcType.ROUND)
    }

    private fun dibujarChimeneaYFrente(gc: GraphicsContext, x: Double, y: Double) {
        gc.fill = Color.web("#14161A")
        gc.fillRect(x + 104.0, y - 14.0, 10.0, 25.0)
        gc.fill = Color.web("#262A30")
        gc.fillPolygon(
            doubleArrayOf(x + 101.0, x + 117.0, x + 114.0, x + 104.0),
            doubleArrayOf(y - 18.0, y - 18.0, y - 12.0, y - 12.0),
            4
        )
        gc.fill = Color.web("#B8860B")
        gc.fillRect(x + 118.0, y + 9.0, 7.0, 9.0)
        gc.fill = Color.web("#FFF59D")
        gc.fillOval(x + 121.0, y + 10.0, 6.0, 7.0)

        gc.fill = Color.web("#7F1D1D")
        gc.fillPolygon(
            doubleArrayOf(x + 116.0, x + 132.0, x + 116.0),
            doubleArrayOf(y + 28.0, y + 42.0, y + 42.0),
            3
        )
        gc.fill = Color.web("#6D1B1B")
        gc.fillRect(x + 2.0, y + 37.0, 116.0, 4.0)
    }

    private fun dibujarRuedasYBielas(gc: GraphicsContext, x: Double, y: Double, tiempoTotal: Double) {
        val angulo = tiempoTotal * 18.0

        for (i in 0..1) {
            val cx = x + 4.0 + i * 16.0
            gc.fill = Color.web("#141414")
            gc.fillOval(cx, y + 39.0, 12.0, 12.0)
            gc.stroke = Color.web("#78909C")
            gc.lineWidth = 1.3
            gc.strokeOval(cx, y + 39.0, 12.0, 12.0)
        }

        for (i in 0..2) {
            val cx = x + 44.0 + i * 22.0
            val cy = y + 36.0
            gc.fill = Color.web("#111111")
            gc.fillOval(cx, cy, 17.0, 17.0)
            gc.stroke = Color.web("#90A4AE")
            gc.lineWidth = 1.8
            gc.strokeOval(cx, cy, 17.0, 17.0)
            gc.strokeLine(cx + 8.5, cy + 8.5, cx + 8.5 + cos(angulo) * 6.0, cy + 8.5 + sin(angulo) * 6.0)
        }

        gc.fill = Color.web("#262A30")
        gc.fillRoundRect(x + 106.0, y + 37.0, 12.0, 9.0, 3.0, 3.0)
        gc.stroke = Color.web("#CFD8DC")
        gc.lineWidth = 2.4
        gc.strokeLine(x + 52.5 + cos(angulo) * 4.5, y + 44.5 + sin(angulo) * 3.5, x + 108.0, y + 41.5)
    }
}
