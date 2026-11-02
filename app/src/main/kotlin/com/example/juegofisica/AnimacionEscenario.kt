package com.example.juegofisica

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.util.Duration
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Gestor de animación continua del escenario para los niveles de MRUV (SRP).
 *
 * Coordina el desplazamiento con efecto parallax del entorno y las vías, el movimiento del
 * jugador corriendo sobre los rieles y la integración con [TrenCarbonWidget] para la
 * locomotora clásica de vapor a carbón y sus columnas de humo.
 *
 * Soporta tres secuencias animadas:
 * 1. **Persecución activa (mientras se calcula)**: Efecto parallax en montañas, árboles, postes
 *    y vías férreas; el jugador corre delante de la locomotora de carbón en aproximación.
 * 2. **Victoria (Desvío del tren)**: La palanca/aguja cambia las vías y la locomotora toma el
 *    carril secundario pasando de largo sin tocar al jugador.
 * 3. **Derrota (Impacto/Atropello)**: La locomotora acelera y alcanza la posición del jugador.
 *
 * @property esModoDificil define si el entorno es nocturno/tormentoso (difícil) o diurno (fácil).
 */
class AnimacionEscenario(
    private val esModoDificil: Boolean = true
) {

    /** Lienzo responsivo donde se dibuja el escenario cuadro a cuadro. */
    val canvas: Canvas = Canvas(400.0, 300.0)

    /** Componente visual dedicado a la locomotora clásica de carbón y su humo (SRP). */
    private val trenCarbon: TrenCarbonWidget = TrenCarbonWidget(esModoDificil)

    /** Bucle de animación continua a 40 FPS. */
    private var timeline: Timeline? = null

    /** Estado actual: 0 = Persecución activa, 1 = Desvío (Victoria), 2 = Impacto (Derrota). */
    private var estado: Int = 0

    /** Tiempo total acumulado en segundos para animar el ciclo de carrera y el parallax. */
    private var tiempoTotal: Double = 0.0

    /** Tiempo transcurrido dentro de la secuencia final (victoria o derrota). */
    private var tiempoSecuenciaFinal: Double = 0.0

    /** Progreso del temporizador de la partida entre 0.0 (inicio) y 1.0 (tiempo agotado). */
    private var progresoTiempo: Double = 0.0

    /** Desplazamiento horizontal acumulado de la capa de montañas (parallax lento). */
    private var offsetMontanas: Double = 0.0

    /** Desplazamiento horizontal acumulado de árboles y postes (parallax medio). */
    private var offsetArboles: Double = 0.0

    /** Desplazamiento horizontal acumulado de durmientes de la vía (parallax rápido). */
    private var offsetVias: Double = 0.0

    /** Posición X normalizada del tren (relativa al ancho del lienzo). */
    private var trenProgresoX: Double = 0.06

    /** Desplazamiento vertical del tren al tomar la vía de desvío en la victoria. */
    private var trenDesvioY: Double = 0.0

    /** Ángulo de rotación del tren durante el cambio de carril. */
    private var trenAngulo: Double = 0.0

    /** Intensidad del temblor de cámara en píxeles. */
    private var temblorCamara: Double = 0.0

    /** Indica si la secuencia final ya notificó su finalización para no repetir el callback. */
    private var secuenciaCompletada: Boolean = false

    /** Acción a ejecutar al concluir la animación de victoria o derrota. */
    private var onSecuenciaCompletada: (() -> Unit)? = null

    /**
     * Vincula las dimensiones del Canvas al contenedor raíz para adaptarse a cualquier tamaño de ventana.
     *
     * @param root el StackPane contenedor de la escena.
     */
    fun vincularTamano(root: StackPane) {
        canvas.widthProperty().bind(root.widthProperty())
        canvas.heightProperty().bind(root.heightProperty())
    }

    /**
     * Inicia el bucle continuo de animación en modo persecución activa.
     */
    fun iniciarPersecucion() {
        estado = 0
        tiempoSecuenciaFinal = 0.0
        trenDesvioY = 0.0
        trenAngulo = 0.0
        secuenciaCompletada = false
        timeline?.stop()

        val delta = 0.025
        timeline = Timeline(
            KeyFrame(Duration.millis(25.0), {
                tiempoTotal += delta
                actualizarEstado(delta)
                renderizar()
            })
        ).apply {
            cycleCount = Timeline.INDEFINITE
            play()
        }
    }

    /**
     * Actualiza el acercamiento gradual de la locomotora según el tiempo restante de la partida.
     *
     * @param progreso valor entre 0.0 (inicio de partida) y 1.0 (tiempo agotado).
     */
    fun actualizarProgreso(progreso: Double) {
        progresoTiempo = progreso.coerceIn(0.0, 1.0)
    }

    /**
     * Activa la secuencia animada de victoria donde las agujas cambian de vía y la locomotora
     * de carbón se desvía, salvando al jugador que corre sobre la vía principal.
     *
     * @param onCompletado callback invocado al finalizar la animación de desvío.
     */
    fun ejecutarAnimacionDesvio(onCompletado: () -> Unit) {
        estado = 1
        tiempoSecuenciaFinal = 0.0
        secuenciaCompletada = false
        onSecuenciaCompletada = onCompletado
        if (timeline == null) {
            iniciarPersecucion()
            estado = 1
        }
    }

    /**
     * Activa la secuencia animada de derrota donde la locomotora de carbón acelera
     * y alcanza la posición del jugador sobre las vías.
     *
     * @param onCompletado callback invocado al finalizar la animación de impacto.
     */
    fun ejecutarAnimacionImpacto(onCompletado: () -> Unit) {
        estado = 2
        tiempoSecuenciaFinal = 0.0
        secuenciaCompletada = false
        onSecuenciaCompletada = onCompletado
        if (timeline == null) {
            iniciarPersecucion()
            estado = 2
        }
    }

    /**
     * Detiene por completo el ciclo de animación para liberar recursos al cambiar de escena.
     */
    fun detener() {
        timeline?.stop()
        timeline = null
    }

    /**
     * Actualiza las posiciones de parallax, de la locomotora de carbón y sus efectos de humo.
     */
    private fun actualizarEstado(delta: Double) {
        val w = if (canvas.width > 0) canvas.width else 400.0
        val h = if (canvas.height > 0) canvas.height else 300.0
        val yViaPrincipal = h * 0.76

        val factorVelocidad = when (estado) {
            1 -> (1.0 - (tiempoSecuenciaFinal / 2.6).coerceIn(0.0, 0.7))
            2 -> 1.6
            else -> 1.0
        }

        offsetMontanas = (offsetMontanas + 22.0 * factorVelocidad * delta) % (w + 200.0)
        offsetArboles = (offsetArboles + 85.0 * factorVelocidad * delta) % 180.0
        offsetVias = (offsetVias + 210.0 * factorVelocidad * delta) % 24.0

        if (estado == 0) {
            val oscilacion = sin(tiempoTotal * 3.0) * 0.012
            trenProgresoX = 0.05 + (progresoTiempo * 0.31) + oscilacion
            trenDesvioY = 0.0
            trenAngulo = 0.0
            temblorCamara = 0.6 + progresoTiempo * 1.4
        } else if (estado == 1) {
            tiempoSecuenciaFinal += delta
            trenProgresoX += 0.34 * delta

            val desvioObjetivo = -h * 0.19
            if (trenProgresoX >= 0.28) {
                val avanceDesvio = ((trenProgresoX - 0.28) / 0.28).coerceIn(0.0, 1.0)
                trenDesvioY = desvioObjetivo * avanceDesvio
                trenAngulo = if (avanceDesvio < 0.85) -12.0 * sin(avanceDesvio * Math.PI) else 0.0
            }
            temblorCamara = 1.2

            if (tiempoSecuenciaFinal >= 2.5 && !secuenciaCompletada) {
                secuenciaCompletada = true
                onSecuenciaCompletada?.invoke()
            }
        } else if (estado == 2) {
            tiempoSecuenciaFinal += delta
            val xJugadorNorm = 0.72
            if (trenProgresoX < xJugadorNorm - 0.05) {
                trenProgresoX += 0.48 * delta
                temblorCamara = Random.nextDouble(2.5, 5.5)
            } else {
                trenProgresoX += 0.12 * delta
                temblorCamara = if (tiempoSecuenciaFinal < 2.0) Random.nextDouble(6.0, 11.0) else 1.5
            }

            if (tiempoSecuenciaFinal >= 2.3 && !secuenciaCompletada) {
                secuenciaCompletada = true
                onSecuenciaCompletada?.invoke()
            }
        }

        val xTrenActual = w * trenProgresoX
        val yTrenActual = yViaPrincipal + trenDesvioY
        trenCarbon.actualizarEfectos(delta, xTrenActual, yTrenActual, estado)
    }

    /**
     * Renderiza todas las capas del escenario, las vías, la palanca de desvío, el jugador y la locomotora.
     */
    private fun renderizar() {
        val gc = canvas.graphicsContext2D
        val w = canvas.width
        val h = canvas.height
        if (w <= 0 || h <= 0) return

        val shakeX = if (temblorCamara > 0) (Random.nextDouble() - 0.5) * temblorCamara * 2.0 else 0.0
        val shakeY = if (temblorCamara > 0) (Random.nextDouble() - 0.5) * temblorCamara * 2.0 else 0.0

        gc.save()
        gc.translate(shakeX, shakeY)

        // 1. Cielo según la dificultad
        val cielo = if (esModoDificil) {
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
        gc.fill = cielo
        gc.fillRect(0.0, 0.0, w, h)

        // 2. Capa Parallax Lenta: Montañas / colinas lejanas
        dibujarMontanasParallax(gc, w, h)

        // 3. Suelo
        val yHorizonte = h * 0.54
        gc.fill = if (esModoDificil) Color.web("#220d0b") else Color.web("#388E3C")
        gc.fillRect(0.0, yHorizonte, w, h - yHorizonte)

        // 4. Capa Parallax Media: Árboles y postes ferroviarios
        dibujarArbolesYPostesParallax(gc, w, yHorizonte)

        val yViaPrincipal = h * 0.76
        val yViaDesvio = yViaPrincipal - h * 0.19

        // 5. Vía secundaria de desvío
        dibujarViaDesvio(gc, w, yViaPrincipal, yViaDesvio)

        // 6. Capa Parallax Rápida: Vía principal con durmientes en movimiento continuo
        dibujarViaPrincipalParallax(gc, w, yViaPrincipal)

        // 7. Aguja / Palanca de cambio de vía
        dibujarPalancaCambioVia(gc, w * 0.46, yViaPrincipal)

        val xTren = w * trenProgresoX
        val yTren = yViaPrincipal + trenDesvioY
        val xJugador = w * 0.74

        // 8. Locomotora de carbón y jugador en movimiento
        if (estado == 1) {
            gc.save()
            gc.translate(xTren + 60.0, yTren - 24.0)
            gc.rotate(trenAngulo)
            trenCarbon.dibujarLocomotora(gc, -60.0, -24.0, tiempoTotal)
            gc.restore()

            dibujarJugador(gc, xJugador, yViaPrincipal - 6.0)
        } else {
            val alcanzado = estado == 2 && (trenProgresoX + 0.22 >= 0.72)
            if (!alcanzado) {
                dibujarJugador(gc, xJugador, yViaPrincipal - 6.0)
            }

            gc.save()
            gc.translate(xTren + 60.0, yTren - 24.0)
            gc.rotate(trenAngulo)
            trenCarbon.dibujarLocomotora(gc, -60.0, -24.0, tiempoTotal)
            gc.restore()
        }

        // 9. Nubes de humo negro/gris de carbón y chispas
        trenCarbon.dibujarParticulas(gc)

        // 10. Destello de impacto cuando la locomotora alcanza al jugador en la derrota
        if (estado == 2 && tiempoSecuenciaFinal in 0.65..1.25) {
            gc.fill = Color.rgb(255, 40, 20, 0.38)
            gc.fillRect(0.0, 0.0, w, h)
        }

        gc.restore()
    }

    /**
     * Dibuja dos ciclos de siluetas de montañas desplazándose lentamente hacia la izquierda.
     */
    private fun dibujarMontanasParallax(gc: GraphicsContext, w: Double, h: Double) {
        gc.fill = if (esModoDificil) Color.web("#180505") else Color.web("#2E7D32")
        val anchoCiclo = w + 200.0
        for (ciclo in 0..1) {
            val base = -offsetMontanas + ciclo * anchoCiclo
            gc.fillPolygon(
                doubleArrayOf(
                    base,
                    base + anchoCiclo * 0.2,
                    base + anchoCiclo * 0.45,
                    base + anchoCiclo * 0.75,
                    base + anchoCiclo,
                    base + anchoCiclo,
                    base
                ),
                doubleArrayOf(
                    h * 0.55,
                    h * 0.38,
                    h * 0.52,
                    h * 0.35,
                    h * 0.54,
                    h * 0.56,
                    h * 0.56
                ),
                7
            )
        }
    }

    /**
     * Dibuja árboles y postes de catenaria desplazándose a velocidad media para dar profundidad.
     */
    private fun dibujarArbolesYPostesParallax(gc: GraphicsContext, w: Double, yHorizonte: Double) {
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

    /**
     * Dibuja el carril secundario de desvío que se bifurca hacia arriba desde la aguja.
     */
    private fun dibujarViaDesvio(gc: GraphicsContext, w: Double, yViaPrincipal: Double, yViaDesvio: Double) {
        val xInicioAguja = w * 0.36
        val xFinCurva = w * 0.62

        gc.fill = Color.web("#29130c")
        var xDurm = xFinCurva
        while (xDurm < w + 24.0) {
            gc.fillRect(xDurm - (offsetVias * 0.5), yViaDesvio - 2.0, 7.0, 22.0)
            xDurm += 22.0
        }

        val colorRielDesvio = if (estado == 1) Color.web("#76FF03") else Color.web("#6d6d6d")
        gc.stroke = colorRielDesvio
        gc.lineWidth = if (estado == 1) 3.0 else 2.0

        gc.strokeLine(xInicioAguja, yViaPrincipal + 4.0, xFinCurva, yViaDesvio + 4.0)
        gc.strokeLine(xFinCurva, yViaDesvio + 4.0, w, yViaDesvio + 4.0)

        gc.strokeLine(xInicioAguja, yViaPrincipal + 16.0, xFinCurva, yViaDesvio + 16.0)
        gc.strokeLine(xFinCurva, yViaDesvio + 16.0, w, yViaDesvio + 16.0)
    }

    /**
     * Dibuja la vía férrea principal con balasto, durmientes desplazándose y rieles de acero.
     */
    private fun dibujarViaPrincipalParallax(gc: GraphicsContext, w: Double, yVia: Double) {
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

    /**
     * Dibuja la palanca/aguja ferroviaria que cambia de rojo (vía recta) a verde (desvío activo).
     */
    private fun dibujarPalancaCambioVia(gc: GraphicsContext, x: Double, yVia: Double) {
        gc.fill = Color.web("#444444")
        gc.fillRect(x - 8.0, yVia + 20.0, 16.0, 8.0)

        gc.stroke = Color.web("#CCCCCC")
        gc.lineWidth = 3.0
        val inclinacionX = if (estado == 1) 10.0 else -8.0
        gc.strokeLine(x, yVia + 22.0, x + inclinacionX, yVia - 4.0)

        gc.fill = if (estado == 1) Color.web("#00FF00") else Color.web("#FF2222")
        gc.fillOval(x + inclinacionX - 5.0, yVia - 10.0, 10.0, 10.0)
    }

    /**
     * Dibuja al personaje del jugador corriendo sobre las vías delante del tren (o festejando al salvarse).
     */
    private fun dibujarJugador(gc: GraphicsContext, x: Double, yPies: Double) {
        val festejando = estado == 1 && trenProgresoX > 0.52
        val faseCarrera = tiempoTotal * 16.0
        val senFase = sin(faseCarrera)
        val reboteBase = if (senFase < 0.0) -senFase else senFase
        val senFestejo = sin(tiempoTotal * 8.0)
        val reboteFestejo = if (senFestejo < 0.0) -senFestejo else senFestejo
        val reboteY = if (festejando) reboteFestejo * -6.0 else reboteBase * -4.0

        val yBase = yPies + reboteY

        gc.fill = Color.rgb(0, 0, 0, 0.45)
        gc.fillOval(x - 10.0, yPies - 3.0, 20.0, 6.0)

        gc.stroke = Color.web("#1E3A5F")
        gc.lineWidth = 3.5
        if (festejando) {
            gc.strokeLine(x, yBase - 14.0, x - 6.0, yBase)
            gc.strokeLine(x, yBase - 14.0, x + 6.0, yBase)
        } else {
            val oscPierna = sin(faseCarrera) * 10.0
            gc.strokeLine(x, yBase - 14.0, x + oscPierna, yBase)
            gc.strokeLine(x, yBase - 14.0, x - oscPierna, yBase)
        }

        gc.fill = Color.web("#00E5FF")
        gc.fillRoundRect(x - 6.0, yBase - 29.0, 12.0, 16.0, 5.0, 5.0)

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

        gc.fill = Color.web("#FFCCAA")
        gc.fillOval(x - 5.5, yBase - 40.0, 11.0, 11.0)

        gc.fill = Color.web("#222222")
        gc.fillArc(x - 6.0, yBase - 41.0, 12.0, 8.0, 0.0, 180.0, javafx.scene.shape.ArcType.ROUND)
    }
}
