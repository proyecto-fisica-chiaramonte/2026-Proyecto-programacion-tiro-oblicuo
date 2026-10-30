package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Stage

/**
 * Pantalla correspondiente al modo difícil del nivel MRUV.
 *
 * Presenta una animación continua en tiempo real ([AnimacionEscenario]) como fondo dinámico,
 * donde las vías se desplazan con efecto parallax, el jugador corre sobre los rieles y el tren
 * lo persigue acortando distancia a medida que avanza el temporizador.
 *
 * Superpone un botón para volver al menú, una calculadora compacta y la casilla de respuesta
 * en la esquina superior izquierda, un botón "REGLAS" y la columna de datos físicos
 * (a, d, v, v₀, x₀) en la esquina superior derecha, y el temporizador en la esquina inferior izquierda.
 *
 * Implementa la interfaz [PantallaJuego] (LSP).
 * Depende de la abstracción [IValidadorRespuesta] (DIP) y delega el renderizado gráfico
 * en [AnimacionEscenario] (SRP).
 *
 * @property stage el Stage principal de la aplicación, usado para cambiar de escena.
 * @property menuScene la Scene del menú principal a la que se regresa.
 * @property validador validador de cálculo físico desacoplado mediante interfaz.
 */
class PantallaModoDificil(
    private val stage: Stage,
    private val menuScene: Scene,
    private val validador: IValidadorRespuesta = ValidadorRespuestaMRUV()
) : PantallaJuego {

    /** Proveedor reutilizable de componentes visuales (DRY). */
    private val componentesUI: ComponentesUI = ComponentesUI()

    /** Gestor del ciclo de animación continua del escenario, jugador y tren (SRP). */
    private val animacionEscenario: AnimacionEscenario = AnimacionEscenario()

    /** Duración fija de la partida en el modo difícil (2 minutos). */
    val tiempoInicialSegundos: Int = 120

    /** Segundos restantes de la partida en curso. */
    var tiempoRestanteSegundos: Int = 120
        private set

    /** Aceleración del problema (m/s²). */
    var aceleracion: Double = 0.0
        private set

    /** Distancia del problema (m). */
    var distancia: Double = 0.0
        private set

    /** Velocidad constante para el cálculo del tiempo (m/s). */
    var velocidad: Double = 0.0
        private set

    /** Velocidad inicial del problema (m/s). */
    var velocidadInicial: Double = 0.0
        private set

    /** Posición inicial del problema (m). */
    var posicionInicial: Double = 0.0
        private set

    /** Resultado teórico esperado de x(t). */
    var resultadoEsperado: Double = 0.0
        private set

    /** Temporizador de cuenta regresiva de la partida. */
    private var temporizador: TemporizadorPartida? = null

    /**
     * Construye y devuelve la Scene del modo difícil con el escenario animado en bucle continuo,
     * los controles de cálculo, el temporizador y las transiciones animadas de victoria y derrota.
     *
     * @return la Scene lista para asignarse al Stage principal.
     */
    override fun crearEscena(): Scene {
        val root = StackPane()

        // Escenario animado continuo (parallax de vías + jugador corriendo + tren persiguiendo)
        animacionEscenario.vincularTamano(root)
        animacionEscenario.iniciarPersecucion()

        // Botón para volver al menú principal (esquina superior izquierda)
        val btnVolver = componentesUI.crearBotonVolver {
            temporizador?.detener()
            animacionEscenario.detener()
            stage.scene = menuScene
        }

        // Calculadora compacta debajo del botón Volver al Menú
        val calculadora = CalculadoraWidget()

        // Overlay de derrota ("¡Perdiste!") que se muestra al concluir la animación de impacto
        val pantallaDerrota = PantallaDerrotaMRUV(
            onReiniciar = {
                temporizador?.detener()
                animacionEscenario.detener()
                stage.scene = menuScene
                stage.title = "Juego Educativo de Física"
            },
            mensaje = "¡Perdiste!"
        )

        // Casilla interactiva compacta para responder x(t) ubicada debajo de la calculadora
        lateinit var casillaRespuesta: CasillaRespuestaWidget
        casillaRespuesta = CasillaRespuestaWidget(
            onVerificar = { entrada ->
                val esCorrecto = validador.validar(entrada, resultadoEsperado)
                if (esCorrecto) {
                    temporizador?.detener()
                    casillaRespuesta.deshabilitar()
                    animacionEscenario.ejecutarAnimacionDesvio {
                        mostrarVictoria()
                    }
                } else if (tiempoRestanteSegundos <= 1) {
                    temporizador?.detener()
                    casillaRespuesta.deshabilitar()
                    animacionEscenario.ejecutarAnimacionImpacto {
                        pantallaDerrota.mostrar()
                    }
                }
                esCorrecto
            }
        )

        // Columna izquierda: volver + calculadora + casilla de respuesta
        val zonaIzquierda = VBox(6.0).apply {
            alignment = Pos.TOP_LEFT
            maxWidth = Region.USE_PREF_SIZE
            children.addAll(btnVolver, calculadora.contenedor, casillaRespuesta.contenedor)
        }

        // Cuadro emergente de reglas reutilizable con las fórmulas de MRUV
        val cuadroReglas = CuadroReglas("x(t) = x₀ + v₀ · t + ½ · a · t²", "t = d / v")

        // Botón para mostrar reglas del modo difícil (esquina superior derecha)
        val btnReglas = componentesUI.crearBotonReglas { cuadroReglas.mostrar() }

        // Datos físicos aleatorios (sin t: el jugador debe calcularlo)
        generarDatosPartida()

        // Columna compacta de variables debajo del botón REGLAS
        val panelDatos = componentesUI.crearPanelDatos(
            componentesUI.crearEtiquetaDato("a = ${aceleracion.toInt()} m/s²"),
            componentesUI.crearEtiquetaDato("d = ${distancia.toInt()} m"),
            componentesUI.crearEtiquetaDato("v = ${velocidad.toInt()} m/s"),
            componentesUI.crearEtiquetaDato("v₀ = ${velocidadInicial.toInt()} m/s"),
            componentesUI.crearEtiquetaDato("x₀ = ${posicionInicial.toInt()} m")
        )

        // Columna superior derecha: REGLAS + panel de datos debajo
        val zonaDerecha = VBox(10.0).apply {
            alignment = Pos.TOP_RIGHT
            maxWidth = Region.USE_PREF_SIZE
            children.addAll(btnReglas, panelDatos)
        }

        // Temporizador de texto limpio en la esquina inferior izquierda
        val lblContador = Label("").apply {
            font = Font.font("System", FontWeight.BOLD, 22.0)
            textFill = Color.web("#FFD700")
        }

        // Inicializar gestor del temporizador sincronizado con el acercamiento del tren
        val gestorTemporizador = TemporizadorPartida(
            onTick = { segundos, tiempoFormateado ->
                tiempoRestanteSegundos = segundos
                lblContador.text = tiempoFormateado
                val progreso = (tiempoInicialSegundos - segundos).toDouble() / tiempoInicialSegundos.toDouble()
                animacionEscenario.actualizarProgreso(progreso)
            },
            onFin = {
                tiempoRestanteSegundos = 0
                lblContador.text = "00:00"
                lblContador.textFill = Color.web("#FF4444")
                casillaRespuesta.deshabilitar()
                animacionEscenario.ejecutarAnimacionImpacto {
                    pantallaDerrota.mostrar()
                }
            }
        )
        temporizador = gestorTemporizador

        // Iniciar la partida automáticamente con 2 minutos fijos
        lblContador.text = gestorTemporizador.formatearTiempo(tiempoInicialSegundos)
        gestorTemporizador.iniciar(tiempoInicialSegundos)

        // Apilar: lienzo animado, controles izquierdos, datos derechos, temporizador, reglas y derrota
        root.children.addAll(
            animacionEscenario.canvas,
            zonaIzquierda,
            zonaDerecha,
            lblContador,
            cuadroReglas.contenedor,
            pantallaDerrota.contenedor
        )

        StackPane.setAlignment(zonaIzquierda, Pos.TOP_LEFT)
        StackPane.setMargin(zonaIzquierda, Insets(15.0))

        StackPane.setAlignment(zonaDerecha, Pos.TOP_RIGHT)
        StackPane.setMargin(zonaDerecha, Insets(15.0))

        StackPane.setAlignment(lblContador, Pos.BOTTOM_LEFT)
        StackPane.setMargin(lblContador, Insets(15.0))

        StackPane.setAlignment(cuadroReglas.contenedor, Pos.CENTER)
        StackPane.setAlignment(pantallaDerrota.contenedor, Pos.CENTER)

        return Scene(root, 400.0, 300.0)
    }

    /**
     * Genera valores enteros aleatorios para a, d, v, v₀ y x₀.
     * Genera una velocidad v (entre 5 y 30 m/s) y un tiempo t entero de modo que
     * d = v · t resulte un valor exacto y t = d / v sea entero y fácil de calcular.
     */
    private fun generarDatosPartida() {
        val v = (5..30).random()
        val tiempoExacto = listOf(2, 3, 4, 5, 6, 8, 10).random()
        velocidad = v.toDouble()
        distancia = (v * tiempoExacto).toDouble()
        aceleracion = (1..10).random().toDouble()
        velocidadInicial = (0..20).random().toDouble()
        posicionInicial = (0..50).random().toDouble()
        resultadoEsperado = validador.calcularPosicionFinal(
            posicionInicial = posicionInicial,
            velocidadInicial = velocidadInicial,
            aceleracion = aceleracion,
            distancia = distancia,
            velocidad = velocidad
        )
    }

    /**
     * Detiene el cronómetro y la animación activa, calcula el tiempo empleado
     * y muestra la pantalla final de victoria.
     */
    fun mostrarVictoria() {
        val tiempoEmpleado = temporizador?.tiempoEmpleadoSegundos ?: (tiempoInicialSegundos - tiempoRestanteSegundos)
        temporizador?.detener()
        animacionEscenario.detener()
        val pantallaVictoria = PantallaVictoriaMRUV(stage, menuScene, tiempoEmpleado)
        stage.scene = pantallaVictoria.crearEscena()
        stage.title = "MRUV - ¡Victoria!"
    }
}
