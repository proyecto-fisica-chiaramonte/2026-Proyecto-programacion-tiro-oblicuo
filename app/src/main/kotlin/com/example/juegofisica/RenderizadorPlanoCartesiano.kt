package com.example.juegofisica

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import kotlin.math.max

/**
 * Renderizador dedicado a dibujar el plano cartesiano con fondo negro (`Color.BLACK`),
 * eje X azul (`Color.BLUE`), eje Y rojo (`Color.RED`), grilla de 10m en 10m, estela punteada
 * blanca y pelota blanca (`Color.WHITE`) para la simulación de Tiro Oblicuo (SRP).
 */
class RenderizadorPlanoCartesiano {

    /** Coordenada X en píxeles del origen (0, 0). */
    private var origenX: Double = 50.0

    /** Coordenada Y en píxeles del origen (0, 0). */
    private var origenY: Double = 250.0

    /** Ancho útil de dibujo del eje X en píxeles. */
    private var anchoUtil: Double = 300.0

    /** Alto útil de dibujo del eje Y en píxeles. */
    private var altoUtil: Double = 200.0

    /** Alcance máximo visible del eje X en metros (múltiplo de 10m). */
    private var maxEscalaX: Double = 100.0

    /** Altura máxima visible del eje Y en metros (múltiplo de 10m). */
    private var maxEscalaY: Double = 60.0

    /** Factor horizontal de conversión en píxeles por metro. */
    private var pixelesPorMetroX: Double = 3.0

    /** Factor vertical de conversión en píxeles por metro. */
    private var pixelesPorMetroY: Double = 3.0

    /**
     * Actualiza las dimensiones activas del lienzo y los factores de escala metros-a-píxeles.
     */
    fun configurarVista(canvas: Canvas, escalaMaxX: Double, escalaMaxY: Double) {
        origenX = 50.0
        origenY = canvas.height - 42.0
        anchoUtil = max(50.0, canvas.width - origenX - 24.0)
        altoUtil = max(50.0, origenY - 24.0)
        maxEscalaX = max(10.0, escalaMaxX)
        maxEscalaY = max(10.0, escalaMaxY)
        pixelesPorMetroX = anchoUtil / maxEscalaX
        pixelesPorMetroY = altoUtil / maxEscalaY
    }

    /**
     * Limpia el fondo en negro y dibuja la grilla y los ejes X (azul) e Y (rojo) de 10m en 10m.
     */
    fun dibujarPlanoBase(gc: GraphicsContext, anchoCanvas: Double, altoCanvas: Double) {
        gc.fill = Color.BLACK
        gc.fillRect(0.0, 0.0, anchoCanvas, altoCanvas)
        gc.font = Font.font("System", FontWeight.BOLD, 9.5)
        dibujarLineasGrilla(gc)
        dibujarEjeXAzul(gc)
        dibujarEjeYRojo(gc)
    }

    /**
     * Dibuja la estela blanca punteada a lo largo del recorrido histórico de la pelota.
     */
    fun dibujarEstela(gc: GraphicsContext, estelaX: List<Double>, estelaY: List<Double>) {
        if (estelaX.size <= 1) return
        gc.save()
        gc.stroke = Color.WHITE
        gc.lineWidth = 2.0
        gc.setLineDashes(5.0, 4.0)
        for (i in 0 until estelaX.size - 1) {
            val px1 = origenX + estelaX[i] * pixelesPorMetroX
            val py1 = origenY - estelaY[i] * pixelesPorMetroY
            gc.strokeLine(px1, py1, origenX + estelaX[i + 1] * pixelesPorMetroX, origenY - estelaY[i + 1] * pixelesPorMetroY)
        }
        gc.restore()
    }

    /**
     * Dibuja la pelota blanca en su posición física actual (pelotaX, pelotaY).
     */
    fun dibujarPelota(gc: GraphicsContext, pelotaX: Double, pelotaY: Double) {
        val radio = 6.0
        val px = origenX + pelotaX * pixelesPorMetroX
        val py = origenY - pelotaY * pixelesPorMetroY
        gc.fill = Color.WHITE
        gc.fillOval(px - radio, py - radio, radio * 2.0, radio * 2.0)
    }

    private fun dibujarLineasGrilla(gc: GraphicsContext) {
        gc.stroke = Color.web("#181818")
        gc.lineWidth = 1.0
        for (mX in 10..maxEscalaX.toInt() step 10) {
            val px = origenX + mX * pixelesPorMetroX
            gc.strokeLine(px, origenY, px, origenY - altoUtil)
        }
        for (mY in 10..maxEscalaY.toInt() step 10) {
            val py = origenY - mY * pixelesPorMetroY
            gc.strokeLine(origenX, py, origenX + anchoUtil, py)
        }
    }

    private fun dibujarEjeXAzul(gc: GraphicsContext) {
        gc.stroke = Color.BLUE; gc.lineWidth = 2.5
        gc.strokeLine(origenX, origenY, origenX + anchoUtil, origenY)
        var ultimoTextoX = -100.0
        for (mX in 10..maxEscalaX.toInt() step 10) {
            val px = origenX + mX * pixelesPorMetroX
            gc.strokeLine(px, origenY - 4.0, px, origenY + 4.0)
            if (px - ultimoTextoX >= 25.0) { gc.fill = Color.LIGHTBLUE; gc.fillText("${mX}m", px - 11.0, origenY + 16.0); ultimoTextoX = px }
        }
        gc.fill = Color.BLUE; gc.fillText("X (m)", origenX + anchoUtil - 26.0, origenY + 31.0)
    }

    private fun dibujarEjeYRojo(gc: GraphicsContext) {
        gc.stroke = Color.RED; gc.lineWidth = 2.5
        gc.strokeLine(origenX, origenY, origenX, origenY - altoUtil)
        var ultimoTextoY = origenY + 100.0
        for (mY in 10..maxEscalaY.toInt() step 10) {
            val py = origenY - mY * pixelesPorMetroY
            gc.strokeLine(origenX - 4.0, py, origenX + 4.0, py)
            if (ultimoTextoY - py >= 13.0) { gc.fill = Color.SALMON; gc.fillText("${mY}m", 4.0, py + 3.5); ultimoTextoY = py }
        }
        gc.fill = Color.RED; gc.fillText("Y (m)", 8.0, origenY - altoUtil - 6.0)
    }
}
