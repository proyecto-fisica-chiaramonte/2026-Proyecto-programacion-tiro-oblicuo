package com.example.juegofisica

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import kotlin.math.cos
import kotlin.math.sin

/**
 * Renderizador gráfico dedicado al entorno parallax (cielo, montañas, árboles, postes,
 * vías férreas, aguja de cambio y jugador corriendo) para los niveles de MRUV (SRP).
 *
 * @property esModoDificil define si la paleta cromática corresponde al modo difícil o fácil.
 */
class RenderizadorEscenarioMRUV(
    private val esModoDificil: Boolean = true
) {

    /**
     * Dibuja el cielo degradado, las montañas, el suelo, los árboles y ambas vías férreas.
     */
    fun dibujarEntornoYVias(
        gc: GraphicsContext,
        w: Double,
        h: Double,
        offsetMontanas: Double,
        offsetArboles: Double,
        offsetVias: Double,
        estado: Int
    ) {
        dibujarCieloYSuelo(gc, w, h, offsetMontanas)
        dibujarArbolesYPostes(gc, w, h * 0.54, offsetArboles)

        val yViaPrincipal = h * 0.76
        val yViaDesvio = yViaPrincipal - h * 0.19
        dibujarViaDesvio(gc, w, yViaPrincipal, yViaDesvio, offsetVias, estado)
        dibujarViaPrincipal(gc, w, yViaPrincipal, offsetVias)
        dibujarPalancaCambioVia(gc, w * 0.46, yViaPrincipal, estado)
    }

    /**
     * Dibuja al personaje del jugador corriendo sobre las vías delante del tren o festejando.
     */
    fun dibujarJugador(
        gc: GraphicsContext,
        x: Double,
        yPies: Double,
        tiempoTotal: Double,
        festejando: Boolean
    ) {
        val faseCarrera = tiempoTotal * 16.0
        val reboteCarrera = kotlin.math.abs(sin(faseCarrera)) * -4.0
        val reboteFestejo = kotlin.math.abs(sin(tiempoTotal * 8.0)) * -6.0
        val yBase = yPies + if (festejando) reboteFestejo else reboteCarrera

        gc.fill = Color.rgb(0, 0, 0, 0.45)
        gc.fillOval(x - 10.0, yPies - 3.0, 20.0, 6.0)

        dibujarPiernasYBrazosJugador(gc, x, yBase, faseCarrera, festejando)

        gc.fill = Color.web("#00E5FF")
        gc.fillRoundRect(x - 6.0, yBase - 29.0, 12.0, 16.0, 5.0, 5.0)
        gc.fill = Color.web("#FFCCAA")
        gc.fillOval(x - 5.5, yBase - 40.0, 11.0, 11.0)
        gc.fill = Color.web("#222222")
        gc.fillArc(x - 6.0, yBase - 41.0, 12.0, 8.0, 0.0, 180.0, javafx.scene.shape.ArcType.ROUND)
    }

    private fun dibujarPiernasYBrazosJugador(
        gc: GraphicsContext,
        x: Double,
        yBase: Double,
        faseCarrera: Double,
        festejando: Boolean
    ) {
        gc.stroke = Color.web("#1E3A5F")
        gc.lineWidth = 3.5
        val oscPierna = if (festejando) 6.0 else sin(faseCarrera) * 10.0
        gc.strokeLine(x, yBase - 14.0, x + oscPierna, yBase)
        gc.strokeLine(x, yBase - 14.0, x - oscPierna, yBase)

        gc.stroke = Color.web("#00B8D4")
        gc.lineWidth = 3.0
        if (festejando) {
            gc.strokeLine(x - 4.0, yBase - 26.0, x - 12.0, yBase - 38.0)
            gc.strokeLine(x + 4.0, yBase - 26.0, x + 12.0, yBase - 38.0)
        } else {
            val oscBrazo = cos(faseCarrera) * 9.0
            gc.strokeLine(x, yBase - 25.0, x + oscBrazo, yBase - 16.0)
            gc.strokeLine(x, yBase - 25.0, x - oscBrazo, yBase - 16.0)
        }
    }

    private fun dibujarCieloYSuelo(gc: GraphicsContext, w: Double, h: Double, offsetMontanas: Double) {
        gc.fill = if (esModoDificil) {
            LinearGradient(
                0.0, 0.0, 0.0, h * 0.65, false, CycleMethod.NO_CYCLE,
                Stop(0.0, Color.web("#140303")),
                Stop(0.55, Color.web("#4a0c0a")),
                Stop(1.0, Color.web("#851f0e"))
            )
        } else {
            LinearGradient(
                0.0, 0.0, 0.0, h * 0.65, false, CycleMethod.NO_CYCLE,
                Stop(0.0, Color.web("#1E88E5")),
                Stop(0.6, Color.web("#64B5F6")),
                Stop(1.0, Color.web("#B3E5FC"))
            )
        }
        gc.fillRect(0.0, 0.0, w, h)

        gc.fill = if (esModoDificil) Color.web("#180505") else Color.web("#2E7D32")
        val anchoCiclo = w + 200.0
        for (ciclo in 0..1) {
            val base = -offsetMontanas + ciclo * anchoCiclo
            gc.fillPolygon(
                doubleArrayOf(base, base + anchoCiclo * 0.2, base + anchoCiclo * 0.45, base + anchoCiclo * 0.75, base + anchoCiclo, base + anchoCiclo, base),
                doubleArrayOf(h * 0.55, h * 0.38, h * 0.52, h * 0.35, h * 0.54, h * 0.56, h * 0.56),
                7
            )
        }

        val yHorizonte = h * 0.54
        gc.fill = if (esModoDificil) Color.web("#220d0b") else Color.web("#388E3C")
        gc.fillRect(0.0, yHorizonte, w, h - yHorizonte)
    }

    private fun dibujarArbolesYPostes(gc: GraphicsContext, w: Double, yHorizonte: Double, offsetArboles: Double) {
        var xElem = -offsetArboles
        while (xElem < w + 180.0) {
            gc.fill = if (esModoDificil) Color.web("#2d1410") else Color.web("#4E342E")
            gc.fillRect(xElem, yHorizonte - 42.0, 4.0, 50.0)
            gc.fillRect(xElem - 8.0, yHorizonte - 40.0, 20.0, 3.0)

            val xArbol = xElem + 85.0
            gc.stroke = if (esModoDificil) Color.web("#1a0806") else Color.web("#4E342E")
            gc.lineWidth = 3.0
            gc.strokeLine(xArbol, yHorizonte + 5.0, xArbol, yHorizonte - 28.0)
            if (esModoDificil) {
                gc.strokeLine(xArbol, yHorizonte - 18.0, xArbol - 10.0, yHorizonte - 30.0)
                gc.strokeLine(xArbol, yHorizonte - 14.0, xArbol + 11.0, yHorizonte - 26.0)
            } else {
                gc.fill = Color.web("#1B5E20")
                gc.fillOval(xArbol - 14.0, yHorizonte - 44.0, 28.0, 24.0)
            }
            xElem += 180.0
        }
    }

    private fun dibujarViaDesvio(
        gc: GraphicsContext,
        w: Double,
        yViaPrincipal: Double,
        yViaDesvio: Double,
        offsetVias: Double,
        estado: Int
    ) {
        val xInicioAguja = w * 0.36
        val xFinCurva = w * 0.62

        gc.fill = Color.web("#29130c")
        var xDurm = xFinCurva
        while (xDurm < w + 24.0) {
            gc.fillRect(xDurm - (offsetVias * 0.5), yViaDesvio - 2.0, 7.0, 22.0)
            xDurm += 22.0
        }

        gc.stroke = if (estado == 1) Color.web("#76FF03") else Color.web("#6d6d6d")
        gc.lineWidth = if (estado == 1) 3.0 else 2.0
        gc.strokeLine(xInicioAguja, yViaPrincipal + 4.0, xFinCurva, yViaDesvio + 4.0)
        gc.strokeLine(xFinCurva, yViaDesvio + 4.0, w, yViaDesvio + 4.0)
        gc.strokeLine(xInicioAguja, yViaPrincipal + 16.0, xFinCurva, yViaDesvio + 16.0)
        gc.strokeLine(xFinCurva, yViaDesvio + 16.0, w, yViaDesvio + 16.0)
    }

    private fun dibujarViaPrincipal(gc: GraphicsContext, w: Double, yVia: Double, offsetVias: Double) {
        gc.fill = Color.web("#331814")
        gc.fillRect(0.0, yVia - 4.0, w, 28.0)

        gc.fill = Color.web("#422013")
        var xDurmiente = -offsetVias
        while (xDurmiente < w + 24.0) {
            gc.fillRect(xDurmiente, yVia - 2.0, 9.0, 24.0)
            xDurmiente += 24.0
        }

        gc.stroke = Color.web("#B0BEC5")
        gc.lineWidth = 3.0
        gc.strokeLine(0.0, yVia + 4.0, w, yVia + 4.0)
        gc.strokeLine(0.0, yVia + 16.0, w, yVia + 16.0)
    }

    private fun dibujarPalancaCambioVia(gc: GraphicsContext, x: Double, yVia: Double, estado: Int) {
        gc.fill = Color.web("#444444")
        gc.fillRect(x - 8.0, yVia + 20.0, 16.0, 8.0)

        val inclinacionX = if (estado == 1) 10.0 else -8.0
        gc.stroke = Color.web("#CCCCCC")
        gc.lineWidth = 3.0
        gc.strokeLine(x, yVia + 22.0, x + inclinacionX, yVia - 4.0)

        gc.fill = if (estado == 1) Color.web("#00FF00") else Color.web("#FF2222")
        gc.fillOval(x + inclinacionX - 5.0, yVia - 10.0, 10.0, 10.0)
    }
}
