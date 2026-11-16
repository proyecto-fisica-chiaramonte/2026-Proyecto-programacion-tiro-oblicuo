package com.example.juegofisica

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Renderizador gráfico dedicado al entorno parallax (cielo, montañas, árboles, postes,
 * vías férreas, aguja de cambio y jugador corriendo) para los niveles de MRUV (SRP).
 */
class RenderizadorEscenarioMRUV(
    private val esModoDificil: Boolean = true
) {

    /** Ancho actual del lienzo en píxeles. */
    private var anchoActual: Double = 400.0

    /** Alto actual del lienzo en píxeles. */
    private var altoActual: Double = 300.0

    /**
     * Configura las dimensiones del lienzo y dibuja el entorno completo y las vías férreas.
     */
    fun dibujarEntornoYVias(gc: GraphicsContext, estado: EstadoAnimacionEscenario, dimensiones: DoubleArray) {
        anchoActual = dimensiones[0]
        altoActual = dimensiones[1]
        dibujarCieloYSuelo(gc, estado.obtenerOffset(0))
        dibujarArbolesYPostes(gc, estado.obtenerOffset(1))
        dibujarViaDesvio(gc, estado.obtenerOffset(2), estado.obtenerEstado())
        dibujarViaPrincipalYPalanca(gc, estado.obtenerOffset(2), estado.obtenerEstado())
    }

    /**
     * Dibuja al personaje del jugador corriendo sobre las vías delante del tren o festejando.
     */
    fun dibujarJugador(gc: GraphicsContext, estado: EstadoAnimacionEscenario) {
        val x = anchoActual * 0.74
        val yPies = altoActual * 0.76 - 6.0
        val festejando = estado.obtenerEstado() == 1 && estado.obtenerCinematicaTren(0) > 0.52
        val fase = estado.obtenerTiempoTotal() * 16.0
        val yBase = yPies + if (festejando) abs(sin(estado.obtenerTiempoTotal() * 8.0)) * -6.0 else abs(sin(fase)) * -4.0

        gc.fill = Color.rgb(0, 0, 0, 0.45)
        gc.fillOval(x - 10.0, yPies - 3.0, 20.0, 6.0)
        dibujarExtremidadesJugador(gc, doubleArrayOf(x, yBase, fase), festejando)
        dibujarTorsoYCabezaJugador(gc, x, yBase)
    }

    private fun dibujarExtremidadesJugador(gc: GraphicsContext, datos: DoubleArray, festejando: Boolean) {
        val x = datos[0]
        val yBase = datos[1]
        val oscPierna = if (festejando) 6.0 else sin(datos[2]) * 10.0
        gc.stroke = if (esModoDificil) Color.web("#1E3A5F") else Color.web("#1565C0")
        gc.lineWidth = 3.5
        gc.strokeLine(x, yBase - 14.0, x + oscPierna, yBase)
        gc.strokeLine(x, yBase - 14.0, x - oscPierna, yBase)

        gc.stroke = Color.web("#00B8D4")
        gc.lineWidth = 3.0
        val dyBrazo = if (festejando) yBase - 38.0 else yBase - 16.0
        val dxBrazo = if (festejando) 12.0 else cos(datos[2]) * 9.0
        gc.strokeLine(x, yBase - 25.0, x - dxBrazo, dyBrazo)
        gc.strokeLine(x, yBase - 25.0, x + dxBrazo, dyBrazo)
    }

    private fun dibujarTorsoYCabezaJugador(gc: GraphicsContext, x: Double, yBase: Double) {
        gc.fill = if (esModoDificil) Color.web("#00E5FF") else Color.web("#00B0FF")
        gc.fillRoundRect(x - 6.0, yBase - 29.0, 12.0, 16.0, 5.0, 5.0)
        gc.fill = Color.web("#FFCCAA")
        gc.fillOval(x - 5.5, yBase - 40.0, 11.0, 11.0)
        gc.fill = Color.web("#222222")
        gc.fillArc(x - 6.0, yBase - 41.0, 12.0, 8.0, 0.0, 180.0, javafx.scene.shape.ArcType.ROUND)
    }

    private fun dibujarCieloYSuelo(gc: GraphicsContext, offsetMontanas: Double) {
        val c1 = if (esModoDificil) Color.web("#140303") else Color.web("#1E88E5")
        val c2 = if (esModoDificil) Color.web("#851f0e") else Color.web("#B3E5FC")
        gc.fill = LinearGradient(0.0, 0.0, 0.0, altoActual * 0.65, false, CycleMethod.NO_CYCLE, Stop(0.0, c1), Stop(1.0, c2))
        gc.fillRect(0.0, 0.0, anchoActual, altoActual)

        gc.fill = if (esModoDificil) Color.web("#180505") else Color.web("#2E7D32")
        val cicloW = anchoActual + 200.0
        for (c in 0..1) {
            val b = -offsetMontanas + c * cicloW
            gc.fillPolygon(doubleArrayOf(b, b + cicloW * 0.25, b + cicloW * 0.5, b + cicloW * 0.75, b + cicloW), doubleArrayOf(altoActual * 0.55, altoActual * 0.36, altoActual * 0.52, altoActual * 0.35, altoActual * 0.55), 5)
        }
        gc.fill = if (esModoDificil) Color.web("#220d0b") else Color.web("#388E3C")
        gc.fillRect(0.0, altoActual * 0.54, anchoActual, altoActual * 0.46)
    }

    private fun dibujarArbolesYPostes(gc: GraphicsContext, offsetArboles: Double) {
        val yHorizonte = altoActual * 0.54
        var xElem = -offsetArboles
        while (xElem < anchoActual + 180.0) {
            gc.fill = if (esModoDificil) Color.web("#2d1410") else Color.web("#4E342E")
            gc.fillRect(xElem, yHorizonte - 42.0, 4.0, 50.0)
            gc.fillRect(xElem - 8.0, yHorizonte - 40.0, 20.0, 3.0)
            gc.fill = if (esModoDificil) Color.web("#1a0806") else Color.web("#1B5E20")
            gc.fillOval(xElem + 71.0, yHorizonte - 42.0, 26.0, 22.0)
            xElem += 180.0
        }
    }

    private fun dibujarViaDesvio(gc: GraphicsContext, offsetVias: Double, estadoModo: Int) {
        val yVia = altoActual * 0.76
        val yDesvio = yVia - altoActual * 0.19
        val xFin = anchoActual * 0.62
        gc.fill = if (esModoDificil) Color.web("#29130c") else Color.web("#3E2723")
        var xDurm = xFin
        while (xDurm < anchoActual + 24.0) { gc.fillRect(xDurm - (offsetVias * 0.5), yDesvio - 2.0, 7.0, 22.0); xDurm += 22.0 }
        gc.stroke = if (estadoModo == 1) Color.web("#76FF03") else Color.web("#6d6d6d")
        gc.lineWidth = if (estadoModo == 1) 3.0 else 2.0
        for (dy in listOf(4.0, 16.0)) { gc.strokeLine(anchoActual * 0.36, yVia + dy, xFin, yDesvio + dy); gc.strokeLine(xFin, yDesvio + dy, anchoActual, yDesvio + dy) }
    }

    private fun dibujarViaPrincipalYPalanca(gc: GraphicsContext, offsetVias: Double, estadoModo: Int) {
        val yVia = altoActual * 0.76
        gc.fill = if (esModoDificil) Color.web("#331814") else Color.web("#4E342E")
        gc.fillRect(0.0, yVia - 4.0, anchoActual, 28.0)
        gc.fill = Color.web("#422013")
        var xDurm = -offsetVias
        while (xDurm < anchoActual + 24.0) { gc.fillRect(xDurm, yVia - 2.0, 9.0, 24.0); xDurm += 24.0 }
        gc.stroke = Color.web("#B0BEC5"); gc.lineWidth = 3.0
        gc.strokeLine(0.0, yVia + 4.0, anchoActual, yVia + 4.0); gc.strokeLine(0.0, yVia + 16.0, anchoActual, yVia + 16.0)
        val xPal = anchoActual * 0.46; val inc = if (estadoModo == 1) 10.0 else -8.0
        gc.strokeLine(xPal, yVia + 22.0, xPal + inc, yVia - 4.0)
        gc.fill = if (estadoModo == 1) Color.web("#00FF00") else Color.web("#FF2222"); gc.fillOval(xPal + inc - 5.0, yVia - 10.0, 10.0, 10.0)
    }
}
