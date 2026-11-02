package com.example.juegofisica

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Componente visual dedicado al renderizado y efectos dinámicos de la locomotora clásica
 * de vapor a carbón (Principio de Responsabilidad Única - SRP).
 *
 * Dibuja una locomotora antigua de hierro negro con vagón tender de carbón, cabina,
 * caldera cilíndrica con bandas de bronce, chimenea destacada, linterna frontal encendida,
 * empujador delantero (cowcatcher) y ruedas motrices con bielas en movimiento.
 * Además, gestiona la emisión continua de columnas de humo negro y gris que se desplazan
 * hacia atrás desde la chimenea y las chispas sobre los rieles.
 *
 * @property esModoDificil indica si se aplica el matiz de brasas intensas del modo difícil.
 */
class TrenCarbonWidget(
    private val esModoDificil: Boolean = true
) {

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
     * Actualiza la emisión continua de humo negro/gris desde la chimenea y las chispas
     * de acuerdo con la posición actual del tren y el estado de la animación.
     *
     * @param delta tiempo transcurrido desde el último cuadro en segundos.
     * @param xTren posición horizontal actual del tren en el lienzo.
     * @param yTren posición vertical base de las ruedas del tren sobre la vía.
     * @param estado estado de la secuencia (0 = persecución, 1 = desvío victoria, 2 = impacto derrota).
     */
    fun actualizarEfectos(delta: Double, xTren: Double, yTren: Double, estado: Int) {
        val xBocaChimenea = xTren + 111.0
        val yBocaChimenea = yTren - 46.0

        // Emisión continua de columnas de humo negro y gris desde la chimenea
        if (estado == 0) {
            emitirHumoChimenea(xBocaChimenea, yBocaChimenea, 2)
            if (Random.nextDouble() < 0.4) {
                emitirChispasRuedas(xTren + 90.0, yTren - 2.0, 2)
            }
        } else if (estado == 1) {
            emitirHumoChimenea(xBocaChimenea, yBocaChimenea, 3)
            emitirChispasRuedas(xTren + 95.0, yTren - 2.0, 4)
        } else if (estado == 2) {
            emitirHumoChimenea(xBocaChimenea, yBocaChimenea, 4)
            emitirChispasRuedas(xTren + 105.0, yTren - 4.0, 7)
        }

        actualizarParticulas(delta, estado)
    }

    /**
     * Dibuja la locomotora clásica de vapor a carbón con su vagón tender, caldera,
     * chimenea, linterna frontal encendida y ruedas con bielas animadas.
     *
     * @param gc contexto gráfico del Canvas.
     * @param x coordenada X local del extremo trasero del tender.
     * @param y coordenada Y local superior de la locomotora.
     * @param tiempoTotal tiempo acumulado en segundos para animar ruedas y bielas.
     */
    fun dibujarLocomotora(gc: GraphicsContext, x: Double, y: Double, tiempoTotal: Double) {
        // 1. Haz de luz cálido de la linterna frontal encendida
        val luzGradiente = RadialGradient(
            0.0, 0.0, x + 124.0, y + 14.0, 145.0, false, CycleMethod.NO_CYCLE,
            Stop(0.0, Color.rgb(255, 230, 120, 0.65)),
            Stop(0.5, Color.rgb(255, 200, 80, 0.25)),
            Stop(1.0, Color.rgb(255, 180, 50, 0.0))
        )
        gc.fill = luzGradiente
        gc.fillPolygon(
            doubleArrayOf(x + 124.0, x + 270.0, x + 270.0),
            doubleArrayOf(y + 14.0, y - 22.0, y + 58.0),
            3
        )

        // 2. Vagón Tender (depósito de carbón trasero)
        // Montículo de carbón mineral negro en la parte superior del tender
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

        // Estructura metálica oscura del vagón tender
        gc.fill = Color.web("#1A1C20")
        gc.fillRoundRect(x, y + 14.0, 35.0, 24.0, 4.0, 4.0)
        gc.stroke = Color.web("#8D6E63")
        gc.lineWidth = 1.2
        gc.strokeRoundRect(x, y + 14.0, 35.0, 24.0, 4.0, 4.0)

        // Enganche de hierro entre el tender y la cabina
        gc.fill = Color.web("#333333")
        gc.fillRect(x + 34.0, y + 28.0, 6.0, 5.0)

        // 3. Cabina clásica del maquinista
        gc.fill = Color.web("#16181C")
        gc.fillRect(x + 39.0, y + 2.0, 28.0, 36.0)

        // Techo arqueado sobresaliente de la cabina
        gc.fill = Color.web("#2C3038")
        gc.fillRoundRect(x + 36.0, y - 1.0, 33.0, 5.0, 4.0, 4.0)

        // Ventana arqueada iluminada por el fuego de la caldera
        gc.fill = Color.web("#FFB300")
        gc.fillRoundRect(x + 45.0, y + 8.0, 15.0, 13.0, 6.0, 6.0)
        gc.stroke = Color.web("#B8860B")
        gc.lineWidth = 1.2
        gc.strokeRoundRect(x + 45.0, y + 8.0, 15.0, 13.0, 6.0, 6.0)

        // 4. Caldera cilíndrica horizontal de vapor (estructura metálica negra/oscura)
        gc.fill = Color.web("#111317")
        gc.fillRoundRect(x + 66.0, y + 10.0, 54.0, 28.0, 8.0, 8.0)

        // Reflejo metálico cilindrico en la caldera
        gc.fill = Color.web("#282C34")
        gc.fillRect(x + 67.0, y + 13.0, 51.0, 5.0)

        // Bandas de bronce/latón características de las locomotoras clásicas
        gc.stroke = Color.web("#C59B27")
        gc.lineWidth = 1.8
        gc.strokeLine(x + 76.0, y + 10.0, x + 76.0, y + 38.0)
        gc.strokeLine(x + 92.0, y + 10.0, x + 92.0, y + 38.0)
        gc.strokeLine(x + 106.0, y + 10.0, x + 106.0, y + 38.0)

        // Domo de vapor redondeado sobre la caldera
        gc.fill = Color.web("#1D2026")
        gc.fillArc(x + 79.0, y + 2.0, 12.0, 16.0, 0.0, 180.0, javafx.scene.shape.ArcType.ROUND)

        // 5. Chimenea destacada clásica con boca acampanada
        gc.fill = Color.web("#14161A")
        gc.fillRect(x + 104.0, y - 14.0, 10.0, 25.0)
        // Corona superior ensanchada de la chimenea
        gc.fill = Color.web("#262A30")
        gc.fillPolygon(
            doubleArrayOf(x + 101.0, x + 117.0, x + 114.0, x + 104.0),
            doubleArrayOf(y - 18.0, y - 18.0, y - 12.0, y - 12.0),
            4
        )

        // 6. Linterna / Faro frontal encendido en la caja de humos
        gc.fill = Color.web("#B8860B")
        gc.fillRect(x + 118.0, y + 9.0, 7.0, 9.0)
        gc.fill = Color.web("#FFF59D")
        gc.fillOval(x + 121.0, y + 10.0, 6.0, 7.0)

        // 7. Empujador / Quitanieves frontal de hierro (Cowcatcher)
        gc.fill = Color.web("#7F1D1D")
        gc.fillPolygon(
            doubleArrayOf(x + 116.0, x + 132.0, x + 116.0),
            doubleArrayOf(y + 28.0, y + 42.0, y + 42.0),
            3
        )
        gc.stroke = Color.web("#9E9E9E")
        gc.lineWidth = 1.2
        gc.strokeLine(x + 118.0, y + 31.0, x + 126.0, y + 42.0)
        gc.strokeLine(x + 121.0, y + 35.0, x + 129.0, y + 42.0)

        // Bastidor inferior rojo oscuro
        gc.fill = Color.web("#6D1B1B")
        gc.fillRect(x + 2.0, y + 37.0, 116.0, 4.0)

        // 8. Ruedas del tender, ruedas motrices grandes y biela mecánica
        dibujarRuedasYBielas(gc, x, y, tiempoTotal)
    }

    /**
     * Dibuja las nubes de humo negro y gris saliendo de la chimenea y las chispas.
     *
     * @param gc contexto gráfico del Canvas.
     */
    fun dibujarParticulas(gc: GraphicsContext) {
        // Dibujar columnas/nubes de humo negro y gris
        for (j in humoOpacidad.indices) {
            val r = humoRadio[j]
            val opacidad = humoOpacidad[j].coerceIn(0.0, 1.0)
            val colorHumo = when (humoTono[j]) {
                0 -> Color.rgb(22, 22, 25, opacidad * 0.78)   // Humo negro de carbón
                1 -> Color.rgb(58, 60, 64, opacidad * 0.68)   // Humo gris carbón oscuro
                else -> Color.rgb(120, 124, 130, opacidad * 0.55) // Vapor gris medio
            }
            gc.fill = colorHumo
            gc.fillOval(humoX[j] - r, humoY[j] - r, r * 2.0, r * 2.0)
        }

        // Dibujar chispas metálicas sobre los rieles
        for (i in chispasVida.indices) {
            val vida = chispasVida[i].coerceIn(0.0, 1.0)
            gc.fill = Color.rgb(255, (165 * vida).toInt() + 75, 20, vida)
            gc.fillOval(chispasX[i], chispasY[i], 3.0, 3.0)
        }
    }

    /**
     * Dibuja las ruedas del vagón tender, las grandes ruedas motrices de hierro y la biela acoplada.
     */
    private fun dibujarRuedasYBielas(gc: GraphicsContext, x: Double, y: Double, tiempoTotal: Double) {
        val angulo = tiempoTotal * 18.0

        // 2 ruedas pequeñas del vagón tender
        for (i in 0..1) {
            val cx = x + 4.0 + i * 16.0
            val cy = y + 39.0
            gc.fill = Color.web("#141414")
            gc.fillOval(cx, cy, 12.0, 12.0)
            gc.stroke = Color.web("#78909C")
            gc.lineWidth = 1.3
            gc.strokeOval(cx, cy, 12.0, 12.0)
        }

        // 3 grandes ruedas motrices de la locomotora de vapor
        for (i in 0..2) {
            val cx = x + 44.0 + i * 22.0
            val cy = y + 36.0
            gc.fill = Color.web("#111111")
            gc.fillOval(cx, cy, 17.0, 17.0)
            gc.stroke = Color.web("#90A4AE")
            gc.lineWidth = 1.8
            gc.strokeOval(cx, cy, 17.0, 17.0)

            // Contrapeso y rayos giratorios
            val rx = cx + 8.5 + cos(angulo) * 6.0
            val ry = cy + 8.5 + sin(angulo) * 6.0
            gc.strokeLine(cx + 8.5, cy + 8.5, rx, ry)
        }

        // Cilindro de vapor delantero y biela motriz horizontal en movimiento
        gc.fill = Color.web("#262A30")
        gc.fillRoundRect(x + 106.0, y + 37.0, 12.0, 9.0, 3.0, 3.0)

        val offsetBielaX = cos(angulo) * 4.5
        val offsetBielaY = sin(angulo) * 3.5
        gc.stroke = Color.web("#CFD8DC")
        gc.lineWidth = 2.4
        gc.strokeLine(
            x + 52.5 + offsetBielaX,
            y + 44.5 + offsetBielaY,
            x + 108.0,
            y + 41.5
        )
    }

    /**
     * Emite bocanadas de humo negro y gris desde la boca de la chimenea.
     */
    private fun emitirHumoChimenea(x: Double, y: Double, cantidad: Int) {
        for (i in 0 until cantidad) {
            humoX.add(x + Random.nextDouble(-3.0, 3.0))
            humoY.add(y + Random.nextDouble(-2.0, 2.0))
            humoRadio.add(Random.nextDouble(5.0, 10.0))
            humoOpacidad.add(0.85)
            humoTono.add(Random.nextInt(0, 3))
        }
    }

    /**
     * Emite partículas de chispas en la base de las ruedas motrices.
     */
    private fun emitirChispasRuedas(x: Double, y: Double, cantidad: Int) {
        for (i in 0 until cantidad) {
            chispasX.add(x + Random.nextDouble(-4.0, 4.0))
            chispasY.add(y + Random.nextDouble(-2.0, 2.0))
            chispasVx.add(Random.nextDouble(-125.0, -35.0))
            chispasVy.add(Random.nextDouble(-85.0, -10.0))
            chispasVida.add(1.0)
        }
    }

    /**
     * Actualiza la física de desplazamiento hacia atrás y expansión de las nubes de humo y chispas.
     */
    private fun actualizarParticulas(delta: Double, estado: Int) {
        val velocidadVientoX = if (estado == 2) 115.0 else 82.0

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
