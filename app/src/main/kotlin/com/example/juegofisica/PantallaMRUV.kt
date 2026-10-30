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
 * Pantalla que se muestra al seleccionar el Modo Fácil del nivel MRUV.
 *
 * Presenta una animación continua en bucle ([AnimacionEscenario]) donde el entorno
 * y las vías se desplazan con efecto parallax, el jugador corre delante del tren y la
 * locomotora lo persigue mientras se resuelve el cálculo del tiempo (t) con un
 * temporizador fijo de 1 minuto (01:00 = 60 segundos).
 *
 * Superpone un botón "Volver al Menú", una calculadora compacta y la casilla de respuesta
 * `t =` en la esquina superior izquierda, un botón "REGLAS" y una columna compacta de
 * variables físicas en la esquina superior derecha, el temporizador en la esquina inferior
 * izquierda y el cuadro emergente con las fórmulas.
 *
 * Implementa la interfaz [PantallaJuego] (LSP) y separa el renderizado gráfico ([AnimacionEscenario]),
 * la validación matemática ([ValidadorTiempoMRUV]) y la calculadora ([CalculadoraWidget]) cumpliendo SRP.
 *
 * @property stage el Stage principal de la aplicación, usado para volver al menú.
 * @property menuScene la Scene del menú principal a la que se regresa.
 * @property validador validador dedicado para el cálculo del tiempo t en MRUV.
 */
class PantallaMRUV(
    private val stage: Stage,
    private val menuScene: Scene,
    private val validador: ValidadorTiempoMRUV = ValidadorTiempoMRUV()
) : PantallaJuego {

    /** Proveedor reutilizable de componentes visuales (DRY). */
    private val componentesUI: ComponentesUI = ComponentesUI()

    /** Gestor del ciclo de animación continua del escenario diurno para el modo fácil (SRP). */
    private val animacionEscenario: AnimacionEscenario = AnimacionEscenario(esModoDificil = false)

    /** Duración máxima fija de la partida en el modo fácil (1 minuto = 60 segundos). */
    val tiempoInicialSegundos: Int = 60

    /** Tiempo límite de juego seleccionado para la partida en segundos (60 segundos). */
    var tiempoSeleccionadoSegundos: Int = 60
        private set

    /** Segundos restantes de la partida en curso (inicia en 60 segundos = 01:00). */
    var tiempoRestanteSegundos: Int = 60
        private set

    /** Aceleración del problema (m/s²). */
    var aceleracion: Double = 0.0
        private set

    /** Distancia del problema (m). */
    var distancia: Double = 0.0
        private set

    /** Tiempo teórico esperado (s) calculado según t = √(2 · d / a). */
    var tiempoEsperado: Double = 0.0
        private set

    /** Velocidad inicial del problema (m/s). */
    var velocidadInicial: Double = 0.0
        private set

    /** Posición inicial del problema (m). */
    var posicionInicial: Double = 0.0
        private set

    /** Temporizador de cuenta regresiva de la partida. */
    private var temporizador: TemporizadorPartida? = null

    /**
     * Construye y devuelve la Scene del modo fácil de MRUV con el escenario animado en bucle,
     * la calculadora, la casilla de respuesta para t, el panel de datos físicos, el temporizador
     * configurado en 01:00 (60 segundos) y las secuencias animadas de victoria y derrota.
     *
     * @return la Scene lista para asignarse al Stage principal.
     */
    override fun crearEscena(): Scene {
        val root = StackPane()

        // Escenario animado en bucle continuo (parallax + jugador corriendo + tren persiguiendo)
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

        // Overlay de derrota ("¡Perdiste!") que se muestra al concluir la animación de atropello
        val pantallaDerrota = PantallaDerrotaMRUV(
            onReiniciar = {
                temporizador?.detener()
                animacionEscenario.detener()
                stage.scene = menuScene
                stage.title = "Juego Educativo de Física"
            },
            mensaje = "¡Perdiste!"
        )

        // Casilla interactiva compacta para responder el tiempo (t =) ubicada debajo de la calculadora
        lateinit var casillaTiempo: CasillaRespuestaWidget
        casillaTiempo = CasillaRespuestaWidget(
            onVerificar = { entrada ->
                val esCorrecto = validador.validar(entrada, tiempoEsperado)
                if (esCorrecto) {
                    temporizador?.detener()
                    casillaTiempo.deshabilitar()
                    animacionEscenario.ejecutarAnimacionDesvio {
                        mostrarVictoria()
                    }
                }
                esCorrecto
            },
            etiquetaTexto = "t ="
        )

        // Columna superior izquierda: volver + calculadora + casilla de respuesta t =
        val zonaIzquierda = VBox(6.0).apply {
            alignment = Pos.TOP_LEFT
            maxWidth = Region.USE_PREF_SIZE
            children.addAll(btnVolver, calculadora.contenedor, casillaTiempo.contenedor)
        }

        // Cuadro emergente de reglas con la fórmula de cálculo del tiempo en MRUV
        val cuadroReglas = CuadroReglas("t = √(2 · d / a)", "x(t) = x₀ + v₀ · t + ½ · a · t²")

        // Botón para mostrar reglas de MRUV (esquina superior derecha)
        val btnReglas = componentesUI.crearBotonReglas { cuadroReglas.mostrar() }

        // Generar variables físicas aleatorias para la partida del modo fácil
        generarDatosPartida()

        // Columna compacta de variables físicas ubicada debajo del botón REGLAS
        val panelDatos = componentesUI.crearPanelDatos(
            componentesUI.crearEtiquetaDato("a = ${aceleracion.toInt()} m/s²"),
            componentesUI.crearEtiquetaDato("d = ${distancia.toInt()} m"),
            componentesUI.crearEtiquetaDato("v₀ = ${velocidadInicial.toInt()} m/s"),
            componentesUI.crearEtiquetaDato("x₀ = ${posicionInicial.toInt()} m")
        )

        // Columna superior derecha: botón REGLAS + panel de datos debajo
        val zonaDerecha = VBox(10.0).apply {
            alignment = Pos.TOP_RIGHT
            maxWidth = Region.USE_PREF_SIZE
            children.addAll(btnReglas, panelDatos)
        }

        // Temporizador de texto limpio en la esquina inferior izquierda en formato MM:SS
        val lblContador = Label("01:00").apply {
            font = Font.font("System", FontWeight.BOLD, 22.0)
            textFill = Color.web("#FFD700")
        }

        // Instanciar el gestor del temporizador sincronizado con el acercamiento del tren
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
                casillaTiempo.deshabilitar()
                animacionEscenario.ejecutarAnimacionImpacto {
                    pantallaDerrota.mostrar()
                }
            }
        )
        temporizador = gestorTemporizador

        // Iniciar automáticamente la cuenta regresiva en 01:00 (60 segundos)
        lblContador.text = gestorTemporizador.formatearTiempo(tiempoInicialSegundos)
        gestorTemporizador.iniciar(tiempoInicialSegundos)

        // Apilar: lienzo animado, zona izquierda, zona derecha, temporizador, reglas y derrota
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
     * Genera valores enteros aleatorios para a (1 a 5 m/s²), d, v₀ (0 a 15 m/s) y x₀ (0 a 20 m)
     * de modo que t = √(2 · d / a) resulte un valor entero exacto y sencillo de calcular.
     */
    private fun generarDatosPartida() {
        val tiempoExacto = listOf(2, 3, 4, 5, 6).random()
        val opcionesAceleracion = listOf(1, 2, 4).filter { a ->
            (a * tiempoExacto * tiempoExacto) % 2 == 0
        }
        val a = if (opcionesAceleracion.isNotEmpty()) opcionesAceleracion.random() else 2
        aceleracion = a.toDouble()
        distancia = (a * tiempoExacto * tiempoExacto / 2).toDouble()
        velocidadInicial = (0..15).random().toDouble()
        posicionInicial = (0..20).random().toDouble()
        tiempoEsperado = validador.calcularTiempo(distancia, aceleracion)
    }

    /**
     * Detiene el temporizador y el bucle de animación, calcula el tiempo empleado sobre la base
     * de 60 segundos (60s - tiempoRestante) y transiciona a la pantalla de victoria.
     */
    fun mostrarVictoria() {
        val tiempoEmpleado = (tiempoInicialSegundos - tiempoRestanteSegundos).coerceIn(0, tiempoInicialSegundos)
        temporizador?.detener()
        animacionEscenario.detener()
        val pantallaVictoria = PantallaVictoriaMRUV(
            stage = stage,
            menuScene = menuScene,
            tiempoEmpleadoSegundos = tiempoEmpleado,
            esModoDificil = false
        )
        stage.scene = pantallaVictoria.crearEscena()
        stage.title = "MRUV - ¡Victoria!"
    }
}
