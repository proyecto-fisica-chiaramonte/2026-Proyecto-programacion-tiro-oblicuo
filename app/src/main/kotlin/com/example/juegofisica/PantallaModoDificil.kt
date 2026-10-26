package com.example.juegofisica

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.util.Duration

/**
 * Pantalla correspondiente al modo difícil del nivel MRUV.
 *
 * Utiliza una imagen JPG como fondo que se adapta al tamaño de la ventana,
 * superpone un botón para volver al menú y una calculadora compacta en la
 * esquina superior izquierda, un botón "REGLAS" y una columna de datos físicos
 * (a, d, v, v₀, x₀) en la esquina superior derecha, inicia automáticamente la cuenta
 * regresiva desde 2 minutos, muestra el texto del temporizador en la esquina
 * inferior izquierda y un cuadro emergente centrado con las fórmulas del MRUV.
 *
 * El tiempo t no se muestra: el jugador debe calcularlo con t = d / v
 * y luego usarlo en x(t) = x₀ + v₀ · t + ½ · a · t².
 *
 * @property stage el Stage principal de la aplicación, usado para volver al menú.
 * @property menuScene la Scene del menú principal a la que se regresa.
 */
class PantallaModoDificil(
    private val stage: Stage,
    private val menuScene: Scene
) {

    /** Duración fija de la partida en el modo difícil (2 minutos). */
    private val tiempoInicialSegundos: Int = 120

    /** Segundos restantes de la partida en curso. */
    var tiempoRestanteSegundos: Int = 120

    /** Aceleración del problema (m/s²). */
    var aceleracion: Double = 0.0

    /** Distancia del problema (m). */
    var distancia: Double = 0.0

    /** Velocidad constante para el cálculo del tiempo (m/s). */
    var velocidad: Double = 0.0

    /** Velocidad inicial del problema (m/s). */
    var velocidadInicial: Double = 0.0

    /** Posición inicial del problema (m). */
    var posicionInicial: Double = 0.0

    /** Resultado teórico esperado de x(t). */
    var resultadoEsperado: Double = 0.0

    /** Validador de cálculo físico y comprobación matemática de MRUV. */
    private val validador: ValidadorRespuestaMRUV = ValidadorRespuestaMRUV()

    /** Temporizador de cuenta regresiva de la partida. */
    private var timeline: Timeline? = null

    /**
     * Construye y devuelve la Scene de la pantalla de modo difícil con la imagen de
     * fondo, los botones de navegación, el temporizador iniciado automáticamente y el
     * cuadro de reglas integrado.
     *
     * @return la Scene lista para asignarse al Stage principal.
     */
    fun crearEscena(): Scene {
        // Contenedor raíz que ocupa toda la ventana y apila los elementos
        val root = StackPane()

        // Cargar la imagen de fondo desde los recursos del proyecto
        val imagenFondo = Image(javaClass.getResource("/images/fondo_mruv_d.jpg").toExternalForm())

        // ImageView que muestra la imagen ajustada al tamaño de la ventana
        val imageView = ImageView(imagenFondo).apply {
            isPreserveRatio = false
            // Vincular ancho y alto al tamaño del StackPane para ser responsive
            fitWidthProperty().bind(root.widthProperty())
            fitHeightProperty().bind(root.heightProperty())
        }

        // Botón para volver al menú principal (esquina superior izquierda)
        val btnVolver = Button("Volver al Menú").apply {
            font = Font.font("System", FontWeight.NORMAL, 14.0)
            style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                    "-fx-border-color: #666666; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 8 16;"
            onMouseEntered = {
                style = "-fx-background-color: #444444; -fx-text-fill: white; " +
                        "-fx-border-color: #888888; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8 16;"
            }
            onMouseExited = {
                style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                        "-fx-border-color: #666666; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8 16;"
            }
            setOnAction {
                timeline?.stop()
                stage.scene = menuScene
            }
        }

        // Calculadora compacta debajo del botón Volver al Menú
        val calculadora = CalculadoraWidget()

        // Casilla interactiva compacta para responder x(t) ubicada justo debajo de la calculadora
        val casillaRespuesta = CasillaRespuestaWidget(
            onVerificar = { entrada ->
                val esCorrecto = validador.validar(entrada, resultadoEsperado)
                if (esCorrecto) {
                    mostrarVictoria()
                }
                esCorrecto
            }
        )

        // Columna izquierda: volver + calculadora + casilla de respuesta justo debajo
        val zonaIzquierda = VBox(6.0).apply {
            alignment = Pos.TOP_LEFT
            maxWidth = Region.USE_PREF_SIZE
            children.addAll(btnVolver, calculadora.contenedor, casillaRespuesta.contenedor)
        }

        // Botón para mostrar reglas del modo difícil (esquina superior derecha)
        val btnReglas = Button("REGLAS").apply {
            font = Font.font("System", FontWeight.BOLD, 14.0)
            style = "-fx-background-color: #1a1a1a; -fx-text-fill: #FFD700; " +
                    "-fx-border-color: #FFD700; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 8 16;"
            onMouseEntered = {
                style = "-fx-background-color: #2a2a00; -fx-text-fill: #FFD700; " +
                        "-fx-border-color: #FFAA00; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8 16;"
            }
            onMouseExited = {
                style = "-fx-background-color: #1a1a1a; -fx-text-fill: #FFD700; " +
                        "-fx-border-color: #FFD700; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8 16;"
            }
        }

        // Datos físicos aleatorios (sin t: el jugador debe calcularlo)
        generarDatosPartida()

        // Columna compacta de variables debajo del botón REGLAS (una línea por dato)
        val panelDatos = VBox(4.0).apply {
            alignment = Pos.TOP_RIGHT
            padding = Insets(8.0, 10.0, 8.0, 10.0)
            maxWidth = Region.USE_PREF_SIZE
            style = "-fx-background-color: rgba(0, 0, 0, 0.75); " +
                    "-fx-border-color: #FFD700; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 8; " +
                    "-fx-background-radius: 8;"
            children.addAll(
                crearEtiquetaDato("a = ${aceleracion.toInt()} m/s²"),
                crearEtiquetaDato("d = ${distancia.toInt()} m"),
                crearEtiquetaDato("v = ${velocidad.toInt()} m/s"),
                crearEtiquetaDato("v₀ = ${velocidadInicial.toInt()} m/s"),
                crearEtiquetaDato("x₀ = ${posicionInicial.toInt()} m")
            )
        }

        // Columna superior derecha: REGLAS + panel de datos debajo, alineados a la derecha
        val zonaDerecha = VBox(10.0).apply {
            alignment = Pos.TOP_RIGHT
            maxWidth = Region.USE_PREF_SIZE
            children.addAll(btnReglas, panelDatos)
        }

        // Cuadro emergente de reglas reutilizable con las fórmulas de MRUV
        val cuadroReglas = CuadroReglas("x(t) = x₀ + v₀ · t + ½ · a · t²", "t = d / v")

        // Overlay de derrota: vuelve al menú principal al reiniciar
        val pantallaDerrota = PantallaDerrotaMRUV(
            onReiniciar = {
                timeline?.stop()
                stage.scene = menuScene
                stage.title = "Juego Educativo de Física"
            }
        )

        // Al hacer clic en REGLAS se muestra el cuadro de reglas
        btnReglas.setOnAction {
            cuadroReglas.mostrar()
        }

        // Temporizador de texto limpio en la esquina inferior izquierda (sin fondo ni contenedor visible)
        val lblContador = Label(formatearTiempo(tiempoInicialSegundos)).apply {
            font = Font.font("System", FontWeight.BOLD, 22.0)
            textFill = Color.web("#FFD700")
        }

        // Iniciar la partida automáticamente con 2 minutos fijos
        tiempoRestanteSegundos = tiempoInicialSegundos
        timeline?.stop()
        timeline = Timeline(
            KeyFrame(Duration.seconds(1.0), {
                if (tiempoRestanteSegundos > 0) {
                    tiempoRestanteSegundos--
                    lblContador.text = formatearTiempo(tiempoRestanteSegundos)
                    // Al llegar a 00:00: detener y reutilizar PantallaDerrotaMRUV (mismo overlay del modo fácil)
                    if (tiempoRestanteSegundos == 0) {
                        timeline?.stop()
                        lblContador.textFill = Color.web("#FF4444")
                        pantallaDerrota.mostrar()
                    }
                }
            })
        ).apply {
            cycleCount = Timeline.INDEFINITE
            play()
        }

        // Apilar: fondo, zona izquierda (volver + calculadora + respuesta), datos, temporizador, reglas y derrota
        root.children.addAll(
            imageView,
            zonaIzquierda,
            zonaDerecha,
            lblContador,
            cuadroReglas.contenedor,
            pantallaDerrota.contenedor
        )

        // Posicionar volver + calculadora + respuesta en la esquina superior izquierda
        StackPane.setAlignment(zonaIzquierda, Pos.TOP_LEFT)
        StackPane.setMargin(zonaIzquierda, Insets(15.0))

        // Posicionar REGLAS + panel de datos en la esquina superior derecha
        StackPane.setAlignment(zonaDerecha, Pos.TOP_RIGHT)
        StackPane.setMargin(zonaDerecha, Insets(15.0))

        // Posicionar el temporizador en la esquina inferior izquierda
        StackPane.setAlignment(lblContador, Pos.BOTTOM_LEFT)
        StackPane.setMargin(lblContador, Insets(15.0))

        // El cuadro de reglas y derrota se centran sobre la imagen
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
     * Detiene el cronómetro y realiza la transición a la pantalla de victoria.
     */
    fun mostrarVictoria() {
        timeline?.stop()
        val tiempoEmpleado = (tiempoInicialSegundos - tiempoRestanteSegundos).coerceAtLeast(0)
        val pantallaVictoria = PantallaVictoriaMRUV(stage, menuScene, tiempoEmpleado)
        stage.scene = pantallaVictoria.crearEscena()
        stage.title = "MRUV - ¡Victoria!"
    }

    /**
     * Crea una etiqueta de dato físico legible sobre el fondo oscuro del panel.
     */
    private fun crearEtiquetaDato(texto: String): Label {
        return Label(texto).apply {
            font = Font.font("System", FontWeight.BOLD, 13.0)
            textFill = Color.web("#FFD700")
        }
    }

    /**
     * Formatea una cantidad de segundos al formato de reloj mm:ss.
     */
    private fun formatearTiempo(segundos: Int): String {
        val minutos = segundos / 60
        val segs = segundos % 60
        return String.format("%02d:%02d", minutos, segs)
    }
}
