package com.example.juegofisica

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import javafx.util.Duration
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Gestor de animación continua del escenario para el modo difícil de MRUV (SRP).
 *
 * Separa el ciclo de renderizado gráfico (Canvas + Timeline en bucle continuo) de la lógica
 * física/matemática y de la interfaz de la calculadora.
 *
 * Soporta tres secuencias animadas:
 * 1. **Persecución activa (mientras se calcula)**: Efecto parallax en montañas, árboles, postes
 *    y vías férreas en desplazamiento continuo; el jugador corre sobre las vías delante del tren
 *    mientras la locomotora lo persigue acortando distancia según avanza el tiempo.
 * 2. **Victoria (Desvío del tren)**: La palanca/aguja cambia las vías y el tren toma el carril
 *    de desvío secundario pasando de largo sin tocar al jugador.
 * 3. **Derrota (Impacto/Atropello)**: El tren acelera a máxima velocidad y alcanza la posición
 *    del jugador sobre la vía principal.
 */
class AnimacionEscenario(
    private val esModoDificil: Boolean = true
) {

    /** Lienzo responsivo donde se dibuja el escenario cuadro a cuadro. */
    val canvas: Canvas = Canvas(400.0, 300.0)

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

    /** Coordenadas X de partículas de chispas. */
    private val chispasX = mutableListOf<Double>()

    /** Coordenadas Y de partículas de chispas. */
    private val chispasY = mutableListOf<Double>()

    /** Velocidades X de partículas de chispas. */
    private val chispasVx = mutableListOf<Double>()

    /** Velocidades Y de partículas de chispas. */
    private val chispasVy = mutableListOf<Double>()

    /** Vida restante (0.0 a 1.0) de partículas de chispas. */
    private val chispasVida = mutableListOf<Double>()

    /** Coordenadas X de partículas de humo. */
    private val humoX = mutableListOf<Double>()

    /** Coordenadas Y de partículas de humo. */
    private val humoY = mutableListOf<Double>()

    /** Radios de partículas de humo. */
    private val humoRadio = mutableListOf<Double>()

    /** Opacidades de partículas de humo. */
    private val humoOpacidad = mutableListOf<Double>()

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
     * Actualiza el acercamiento gradual del tren en función del tiempo restante de la partida.
     *
     * @param progreso valor entre 0.0 (inicio de partida) y 1.0 (tiempo agotado).
     */
    fun actualizarProgreso(progreso: Double) {
        progresoTiempo = progreso.coerceIn(0.0, 1.0)
    }

    /**
     * Activa la secuencia animada de victoria donde las agujas cambian de vía y el tren se desvía,
     * salvando al jugador que corre sobre la vía principal.
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
     * Activa la secuencia animada de derrota donde el tren acelera a máxima velocidad
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
     * Actualiza las posiciones de parallax, del tren, del jugador y de las partículas según el estado.
     */
    private fun actualizarEstado(delta: Double) {
        val w = if (canvas.width > 0) canvas.width else 400.0
        val h = if (canvas.height > 0) canvas.height else 300.0
        val yViaPrincipal = h * 0.76

        // Velocidad de desplazamiento parallax según el estado
        val factorVelocidad = when (estado) {
            1 -> (1.0 - (tiempoSecuenciaFinal / 2.6).coerceIn(0.0, 0.7))
            2 -> 1.6
            else -> 1.0
        }

        offsetMontanas = (offsetMontanas + 22.0 * factorVelocidad * delta) % (w + 200.0)
        offsetArboles = (offsetArboles + 85.0 * factorVelocidad * delta) % 180.0
        offsetVias = (offsetVias + 210.0 * factorVelocidad * delta) % 24.0

        val xTrenActual = w * trenProgresoX
        val yTrenActual = yViaPrincipal + trenDesvioY

        if (estado == 0) {
            // Modo persecución: el tren acorta margen según avanza el tiempo (de 0.05 a 0.36 del ancho)
            val oscilacion = sin(tiempoTotal * 3.0) * 0.012
            trenProgresoX = 0.05 + (progresoTiempo * 0.31) + oscilacion
            trenDesvioY = 0.0
            trenAngulo = 0.0
            temblorCamara = 0.6 + progresoTiempo * 1.4

            if (Random.nextDouble() < 0.65) {
                generarHumo(xTrenActual + 95.0, yTrenActual - 46.0, 1)
            }
            if (Random.nextDouble() < 0.45) {
                generarChispas(xTrenActual + 85.0, yTrenActual - 2.0, 2)
            }
        } else if (estado == 1) {
            // Modo Victoria (Desvío): el tren avanza hasta la aguja y toma el carril superior desviado
            tiempoSecuenciaFinal += delta
            trenProgresoX += 0.34 * delta

            // Al cruzar la aguja (trenProgresoX > 0.32), el tren sube hacia el carril secundario
            val desvioObjetivo = -h * 0.19
            if (trenProgresoX >= 0.28) {
                val avanceDesvio = ((trenProgresoX - 0.28) / 0.28).coerceIn(0.0, 1.0)
                trenDesvioY = desvioObjetivo * avanceDesvio
                trenAngulo = if (avanceDesvio < 0.85) -12.0 * sin(avanceDesvio * Math.PI) else 0.0
                generarChispas(xTrenActual + 90.0, yTrenActual - 2.0, 4)
            }
            temblorCamara = 1.2
            generarHumo(xTrenActual + 95.0, yTrenActual - 46.0, 2)

            if (tiempoSecuenciaFinal >= 2.5 && !secuenciaCompletada) {
                secuenciaCompletada = true
                onSecuenciaCompletada?.invoke()
            }
        } else if (estado == 2) {
            // Modo Derrota (Impacto): el tren acelera violentamente sobre la vía principal y alcanza al jugador
            tiempoSecuenciaFinal += delta
            val xJugadorNorm = 0.72
            if (trenProgresoX < xJugadorNorm - 0.05) {
                trenProgresoX += 0.48 * delta
                temblorCamara = Random.nextDouble(2.5, 5.5)
                generarChispas(xTrenActual + 95.0, yTrenActual - 2.0, 6)
                generarHumo(xTrenActual + 95.0, yTrenActual - 46.0, 3)
            } else {
                // Momento del impacto/alcance al jugador
                trenProgresoX += 0.12 * delta
                temblorCamara = if (tiempoSecuenciaFinal < 2.0) Random.nextDouble(6.0, 11.0) else 1.5
                generarChispas(xTrenActual + 110.0, yTrenActual - 10.0, 9)
                generarHumo(xTrenActual + 100.0, yTrenActual - 35.0, 4)
            }

            if (tiempoSecuenciaFinal >= 2.3 && !secuenciaCompletada) {
                secuenciaCompletada = true
                onSecuenciaCompletada?.invoke()
            }
        }

        actualizarParticulas(delta)
    }

    /**
     * Renderiza todas las capas del escenario, las vías, la palanca de desvío, el jugador y el tren.
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

        // 1. Cielo según la dificultad (tormentoso rojizo en difícil, diurno celeste en fácil)
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

        // 3. Suelo (árido oscuro en difícil, pradera verde en fácil)
        val yHorizonte = h * 0.54
        gc.fill = if (esModoDificil) Color.web("#220d0b") else Color.web("#388E3C")
        gc.fillRect(0.0, yHorizonte, w, h - yHorizonte)

        // 4. Capa Parallax Media: Árboles secos y postes ferroviarios
        dibujarArbolesYPostesParallax(gc, w, yHorizonte)

        val yViaPrincipal = h * 0.76
        val yViaDesvio = yViaPrincipal - h * 0.19

        // 5. Vía secundaria de desvío (visible al activarse la victoria o como ramal de escape)
        dibujarViaDesvio(gc, w, yViaPrincipal, yViaDesvio)

        // 6. Capa Parallax Rápida: Vía principal con durmientes en movimiento continuo
        dibujarViaPrincipalParallax(gc, w, yViaPrincipal)

        // 7. Aguja / Palanca de cambio de vía
        dibujarPalancaCambioVia(gc, w * 0.46, yViaPrincipal)

        val xTren = w * trenProgresoX
        val yTren = yViaPrincipal + trenDesvioY
        val xJugador = w * 0.74

        // Si el tren se desvió por el carril superior (estado == 1), se dibuja detrás del jugador
        if (estado == 1) {
            gc.save()
            gc.translate(xTren + 60.0, yTren - 24.0)
            gc.rotate(trenAngulo)
            dibujarTren(gc, -60.0, -24.0)
            gc.restore()

            dibujarJugador(gc, xJugador, yViaPrincipal - 6.0)
        } else {
            // En persecución o impacto, el jugador corre en la misma vía delante del tren
            val alcanzado = estado == 2 && (trenProgresoX + 0.22 >= 0.72)
            if (!alcanzado) {
                dibujarJugador(gc, xJugador, yViaPrincipal - 6.0)
            }

            gc.save()
            gc.translate(xTren + 60.0, yTren - 24.0)
            gc.rotate(trenAngulo)
            dibujarTren(gc, -60.0, -24.0)
            gc.restore()
        }

        // 8. Partículas de humo y chispas
        dibujarParticulas(gc)

        // 9. Destello rojo de impacto cuando el tren alcanza al jugador en la derrota
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
        var indice = 0
        while (xElem < w + 180.0) {
            // Poste ferroviario
            gc.fill = if (esModoDificil) Color.web("#2d1410") else Color.web("#4E342E")
            gc.fillRect(xElem, yHorizonte - 42.0, 4.0, 50.0)
            gc.fillRect(xElem - 8.0, yHorizonte - 40.0, 20.0, 3.0)

            // Árbol intercalado (seco en difícil, frondoso en fácil)
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
            indice++
        }
    }

    /**
     * Dibuja el carril secundario de desvío que se bifurca hacia arriba desde la aguja.
     */
    private fun dibujarViaDesvio(gc: GraphicsContext, w: Double, yViaPrincipal: Double, yViaDesvio: Double) {
        val xInicioAguja = w * 0.36
        val xFinCurva = w * 0.62

        // Durmientes del carril desviado superior
        gc.fill = Color.web("#29130c")
        var xDurm = xFinCurva
        while (xDurm < w + 24.0) {
            gc.fillRect(xDurm - (offsetVias * 0.5), yViaDesvio - 2.0, 7.0, 22.0)
            xDurm += 22.0
        }

        // Rieles de la bifurcación y vía superior
        val colorRielDesvio = if (estado == 1) Color.web("#76FF03") else Color.web("#6d6d6d")
        gc.stroke = colorRielDesvio
        gc.lineWidth = if (estado == 1) 3.0 else 2.0

        // Riel superior del desvío
        gc.strokeLine(xInicioAguja, yViaPrincipal + 4.0, xFinCurva, yViaDesvio + 4.0)
        gc.strokeLine(xFinCurva, yViaDesvio + 4.0, w, yViaDesvio + 4.0)

        // Riel inferior del desvío
        gc.strokeLine(xInicioAguja, yViaPrincipal + 16.0, xFinCurva, yViaDesvio + 16.0)
        gc.strokeLine(xFinCurva, yViaDesvio + 16.0, w, yViaDesvio + 16.0)
    }

    /**
     * Dibuja la vía férrea principal con balasto, durmientes desplazándose y rieles de acero.
     */
    private fun dibujarViaPrincipalParallax(gc: GraphicsContext, w: Double, yVia: Double) {
        // Balasto de piedra bajo la vía
        gc.fill = Color.web("#331814")
        gc.fillRect(0.0, yVia - 4.0, w, 28.0)

        // Durmientes de madera en movimiento continuo hacia la izquierda
        gc.fill = Color.web("#422013")
        var xDurmiente = -offsetVias
        while (xDurmiente < w + 24.0) {
            gc.fillRect(xDurmiente, yVia - 2.0, 9.0, 24.0)
            xDurmiente += 24.0
        }

        // Rieles de acero continuo
        gc.stroke = Color.web("#B0BEC5")
        gc.lineWidth = 3.0
        gc.strokeLine(0.0, yVia + 4.0, w, yVia + 4.0)
        gc.strokeLine(0.0, yVia + 16.0, w, yVia + 16.0)
    }

    /**
     * Dibuja la palanca/aguja ferroviaria que cambia de rojo (vía recta) a verde (desvío activo).
     */
    private fun dibujarPalancaCambioVia(gc: GraphicsContext, x: Double, yVia: Double) {
        // Base mecánica de la aguja
        gc.fill = Color.web("#444444")
        gc.fillRect(x - 8.0, yVia + 20.0, 16.0, 8.0)

        // Brazo de la palanca inclinado según el estado
        gc.stroke = Color.web("#CCCCCC")
        gc.lineWidth = 3.0
        val inclinacionX = if (estado == 1) 10.0 else -8.0
        gc.strokeLine(x, yVia + 22.0, x + inclinacionX, yVia - 4.0)

        // Luz indicadora de la aguja (Verde = desvío salvador, Rojo = vía de colisión)
        gc.fill = if (estado == 1) Color.web("#00FF00") else Color.web("#FF2222")
        gc.fillOval(x + inclinacionX - 5.0, yVia - 10.0, 10.0, 10.0)
    }

    /**
     * Dibuja al personaje del jugador corriendo sobre las vías delante del tren (o festejando al salvarse).
     */
    private fun dibujarJugador(gc: GraphicsContext, x: Double, yPies: Double) {
        val festejando = estado == 1 && trenProgresoX > 0.52
        val faseCarrera = tiempoTotal * 16.0
        val reboteY = if (festejando) abs(sin(tiempoTotal * 8.0)) * -6.0 else abs(sin(faseCarrera)) * -4.0

        val yBase = yPies + reboteY

        // Sombra bajo el jugador
        gc.fill = Color.rgb(0, 0, 0, 0.45)
        gc.fillOval(x - 10.0, yPies - 3.0, 20.0, 6.0)

        // Piernas en movimiento de carrera
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

        // Torso / Chaqueta brillante para alta visibilidad sobre las vías
        gc.fill = Color.web("#00E5FF")
        gc.fillRoundRect(x - 6.0, yBase - 29.0, 12.0, 16.0, 5.0, 5.0)

        // Brazos en movimiento
        gc.stroke = Color.web("#00B8D4")
        gc.lineWidth = 3.0
        if (festejando) {
            // Brazos en alto celebrando que el tren se desvió
            gc.strokeLine(x - 4.0, yBase - 26.0, x - 12.0, yBase - 38.0)
            gc.strokeLine(x + 4.0, yBase - 26.0, x + 12.0, yBase - 38.0)
        } else {
            val oscBrazo = cos(faseCarrera) * 9.0
            gc.strokeLine(x, yBase - 25.0, x + oscBrazo, yBase - 16.0)
            gc.strokeLine(x, yBase - 25.0, x - oscBrazo, yBase - 16.0)
        }

        // Cabeza
        gc.fill = Color.web("#FFCCAA")
        gc.fillOval(x - 5.5, yBase - 40.0, 11.0, 11.0)

        // Cabello / Gorra
        gc.fill = Color.web("#222222")
        gc.fillArc(x - 6.0, yBase - 41.0, 12.0, 8.0, 0.0, 180.0, javafx.scene.shape.ArcType.ROUND)
    }

    /**
     * Dibuja la locomotora en llamas persiguiendo al jugador con su haz de luz frontal y ruedas girando.
     */
    private fun dibujarTren(gc: GraphicsContext, x: Double, y: Double) {
        // Haz de luz del faro delantero iluminando las vías y al jugador
        val luzGradiente = RadialGradient(
            0.0, 0.0, x + 115.0, y + 18.0, 140.0, false, CycleMethod.NO_CYCLE,
            Stop(0.0, Color.rgb(255, 235, 130, 0.55)),
            Stop(1.0, Color.rgb(255, 235, 130, 0.0))
        )
        gc.fill = luzGradiente
        gc.fillPolygon(
            doubleArrayOf(x + 115.0, x + 260.0, x + 260.0),
            doubleArrayOf(y + 18.0, y - 18.0, y + 58.0),
            3
        )

        // Llamas animadas en el techo del tren
        val altLlama = sin(tiempoTotal * 22.0) * 4.0
        gc.fill = Color.web("#FF5722")
        gc.fillPolygon(
            doubleArrayOf(x + 18.0, x + 34.0, x + 48.0, x + 66.0, x + 82.0, x + 98.0),
            doubleArrayOf(y, y - 16.0 + altLlama, y, y - 22.0 - altLlama, y, y - 14.0 + altLlama),
            6
        )
        gc.fill = Color.web("#FFC107")
        gc.fillPolygon(
            doubleArrayOf(x + 28.0, x + 42.0, x + 55.0, x + 70.0, x + 86.0),
            doubleArrayOf(y, y - 10.0 - altLlama, y, y - 14.0 + altLlama, y),
            5
        )

        // Carrocería principal estilo Renfe
        gc.fill = Color.web("#DEDAD0")
        gc.fillRoundRect(x, y, 114.0, 42.0, 7.0, 7.0)

        // Franja longitudinal púrpura/roja
        gc.fill = Color.web("#8E1B46")
        gc.fillRect(x, y + 27.0, 96.0, 6.0)

        // Frente amarillo/naranja de la locomotora
        gc.fill = Color.web("#E69A10")
        gc.fillRoundRect(x + 88.0, y + 2.0, 28.0, 40.0, 6.0, 6.0)

        // Ventanas laterales y cabina
        gc.fill = Color.web("#2B1B17")
        gc.fillRect(x + 10.0, y + 9.0, 16.0, 13.0)
        gc.fillRect(x + 34.0, y + 9.0, 16.0, 13.0)
        gc.fillRect(x + 58.0, y + 9.0, 16.0, 13.0)
        gc.fillRect(x + 94.0, y + 7.0, 18.0, 13.0)

        // Faro frontal brillante
        gc.fill = Color.web("#FFF59D")
        gc.fillOval(x + 110.0, y + 24.0, 7.0, 7.0)

        // Ruedas con bielas en rotación continua
        gc.fill = Color.web("#181818")
        gc.stroke = Color.web("#888888")
        gc.lineWidth = 1.5
        val anguloRueda = tiempoTotal * 18.0
        for (i in 0..3) {
            val cx = x + 12.0 + (i * 25.0)
            val cy = y + 39.0
            gc.fillOval(cx, cy, 15.0, 15.0)
            gc.strokeOval(cx, cy, 15.0, 15.0)

            // Radio giratorio para visualizar el giro de las ruedas
            val rx = cx + 7.5 + cos(anguloRueda) * 5.5
            val ry = cy + 7.5 + sin(anguloRueda) * 5.5
            gc.strokeLine(cx + 7.5, cy + 7.5, rx, ry)
        }
    }

    /**
     * Calcula el valor absoluto de un número decimal sin requerir funciones externas adicionales.
     */
    private fun abs(valor: Double): Double = if (valor < 0.0) -valor else valor

    /**
     * Genera partículas de chispas en las ruedas del tren.
     */
    private fun generarChispas(x: Double, y: Double, cantidad: Int) {
        for (i in 0 until cantidad) {
            chispasX.add(x + Random.nextDouble(-4.0, 4.0))
            chispasY.add(y + Random.nextDouble(-2.0, 2.0))
            chispasVx.add(Random.nextDouble(-120.0, -30.0))
            chispasVy.add(Random.nextDouble(-85.0, -10.0))
            chispasVida.add(1.0)
        }
    }

    /**
     * Genera partículas de humo que se desplazan hacia atrás por la velocidad del tren.
     */
    private fun generarHumo(x: Double, y: Double, cantidad: Int) {
        for (i in 0 until cantidad) {
            humoX.add(x + Random.nextDouble(-5.0, 5.0))
            humoY.add(y + Random.nextDouble(-3.0, 3.0))
            humoRadio.add(Random.nextDouble(6.0, 13.0))
            humoOpacidad.add(0.7)
        }
    }

    /**
     * Actualiza las partículas de humo y chispas activas.
     */
    private fun actualizarParticulas(delta: Double) {
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

        var j = humoOpacidad.size - 1
        while (j >= 0) {
            humoY[j] -= 26.0 * delta
            humoX[j] -= 65.0 * delta
            humoRadio[j] += 12.0 * delta
            humoOpacidad[j] -= delta * 0.65
            if (humoOpacidad[j] <= 0.0) {
                humoX.removeAt(j)
                humoY.removeAt(j)
                humoRadio.removeAt(j)
                humoOpacidad.removeAt(j)
            }
            j--
        }
    }

    /**
     * Dibuja las partículas de humo y chispas sobre el lienzo.
     */
    private fun dibujarParticulas(gc: GraphicsContext) {
        for (j in humoOpacidad.indices) {
            val r = humoRadio[j]
            gc.fill = Color.rgb(45, 38, 38, humoOpacidad[j].coerceIn(0.0, 1.0) * 0.55)
            gc.fillOval(humoX[j] - r, humoY[j] - r, r * 2.0, r * 2.0)
        }

        for (i in chispasVida.indices) {
            val vida = chispasVida[i].coerceIn(0.0, 1.0)
            gc.fill = Color.rgb(255, (160 * vida).toInt() + 80, 20, vida)
            gc.fillOval(chispasX[i], chispasY[i], 3.0, 3.0)
        }
    }
}
