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
import javafx.scene.layout.HBox
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
 * Pantalla que se muestra al seleccionar "MRUV" desde el menú principal (Modo Fácil).
 *
 * Utiliza una imagen JPG como fondo, superpone un botón "Volver al Menú", una calculadora
 * compacta y una casilla interactiva para responder el valor del tiempo t en la esquina
 * superior izquierda, un botón "REGLAS" y una columna de variables físicas (a, d) en la
 * esquina superior derecha, un selector de tiempo inicial, el texto del temporizador en la
 * esquina inferior izquierda y un cuadro emergente con la fórmula de MRUV: t = √(2 · d / a).
 *
 * @property stage el Stage principal de la aplicación, usado para volver al menú.
 * @property menuScene la Scene del menú principal a la que se regresa.
 */
class PantallaMRUV(
    private val stage: Stage,
    private val menuScene: Scene
) {

    /** Tiempo límite de juego seleccionado para la partida en segundos. */
    var tiempoSeleccionadoSegundos: Int = 180

    /** Segundos restantes de la partida en curso. */
    var tiempoRestanteSegundos: Int = 180

    /** Aceleración del problema (m/s²). */
    var aceleracion: Double = 0.0

    /** Distancia del problema (m). */
    var distancia: Double = 0.0

    /** Tiempo teórico esperado según la fórmula t = √(2 · d / a) en segundos. */
    var tiempoEsperado: Double = 0.0

    /** Validador de cálculo físico y comprobación matemática de tiempo en modo fácil. */
    private val validador: ValidadorTiempoMRUV = ValidadorTiempoMRUV()

    /** Temporizador de cuenta regresiva de la partida. */
    private var timeline: Timeline? = null

    /**
     * Construye y devuelve la Scene de la pantalla de MRUV con la imagen de
     * fondo, los botones de navegación, la calculadora, la casilla de respuesta para t,
     * el panel de datos físicos, el modal de inicio y el temporizador.
     *
     * @return la Scene lista para asignarse al Stage principal.
     */
    fun crearEscena(): Scene {
        // Contenedor raíz que ocupa toda la ventana y apila los elementos
        val root = StackPane()

        // Cargar la imagen de fondo desde los recursos del proyecto
        val imagenFondo = Image(javaClass.getResource("/images/fondo_mruv.jpg").toExternalForm())

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

        // Casilla interactiva compacta para responder el valor de t ubicada justo debajo de la calculadora
        val casillaTiempo = CasillaTiempoWidget(
            onVerificar = { entrada ->
                val esCorrecto = validador.validar(entrada, tiempoEsperado)
                esCorrecto
            }
        )

        // Columna superior izquierda: volver + calculadora + casilla de respuesta t justo debajo
        val zonaIzquierda = VBox(6.0).apply {
            alignment = Pos.TOP_LEFT
            maxWidth = Region.USE_PREF_SIZE
            children.addAll(btnVolver, calculadora.contenedor, casillaTiempo.contenedor)
        }

        // Botón para mostrar reglas de MRUV (esquina superior derecha)
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

        // Generar variables físicas aleatorias de modo fácil
        generarDatosPartida()

        // Columna compacta de variables físicas ubicada debajo del botón REGLAS
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
                crearEtiquetaDato("d = ${distancia.toInt()} m")
            )
        }

        // Columna superior derecha: botón REGLAS + panel de datos debajo, alineados a la derecha
        val zonaDerecha = VBox(10.0).apply {
            alignment = Pos.TOP_RIGHT
            maxWidth = Region.USE_PREF_SIZE
            children.addAll(btnReglas, panelDatos)
        }

        // Cuadro emergente de reglas (centrado, inicialmente oculto) con la fórmula de MRUV
        val cuadroReglas = CuadroReglas("t = √(2 · d / a)")

        // Overlay de derrota: vuelve al menú principal al reiniciar
        val pantallaDerrota = PantallaDerrotaMRUV(
            onReiniciar = {
                timeline?.stop()
                stage.scene = menuScene
                stage.title = "Juego Educativo de Física"
            }
        )

        // Al hacer clic en REGLAS se muestra el cuadro de reglas
        btnReglas.setOnAction { cuadroReglas.mostrar() }

        // Temporizador de texto limpio en la esquina inferior izquierda (sin fondo ni contenedor visible)
        val lblContador = Label("00:00").apply {
            font = Font.font("System", FontWeight.BOLD, 22.0)
            textFill = Color.web("#FFD700")
            isVisible = false
            isManaged = false
        }

        // Modal inicial para seleccionar el tiempo de juego
        val modalTiempo = VBox(15.0).apply {
            alignment = Pos.CENTER
            padding = Insets(20.0, 25.0, 20.0, 25.0)
            maxWidth = 320.0
            maxHeight = 150.0
            style = "-fx-background-color: #000000; " +
                    "-fx-border-color: #555555; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10;"
        }

        val tituloModal = Text("Selecciona el Tiempo").apply {
            font = Font.font("System", FontWeight.BOLD, 18.0)
            fill = Color.WHITE
        }

        val estiloBotonTiempo = "-fx-background-color: #222222; -fx-text-fill: white; " +
                "-fx-border-color: #666666; -fx-border-radius: 5; " +
                "-fx-background-radius: 5; -fx-padding: 8 12; -fx-font-weight: bold;"

        val estiloBotonHover = "-fx-background-color: #005500; -fx-text-fill: #00FF00; " +
                "-fx-border-color: #00FF00; -fx-border-radius: 5; " +
                "-fx-background-radius: 5; -fx-padding: 8 12; -fx-font-weight: bold;"

        val btn3Min = Button("3 minutos").apply {
            font = Font.font("System", FontWeight.BOLD, 12.0)
            style = estiloBotonTiempo
            onMouseEntered = { style = estiloBotonHover }
            onMouseExited = { style = estiloBotonTiempo }
        }

        val btn5Min = Button("5 minutos").apply {
            font = Font.font("System", FontWeight.BOLD, 12.0)
            style = estiloBotonTiempo
            onMouseEntered = { style = estiloBotonHover }
            onMouseExited = { style = estiloBotonTiempo }
        }

        val btn10Min = Button("10 minutos").apply {
            font = Font.font("System", FontWeight.BOLD, 12.0)
            style = estiloBotonTiempo
            onMouseEntered = { style = estiloBotonHover }
            onMouseExited = { style = estiloBotonTiempo }
        }

        // Lógica de inicio de partida al seleccionar tiempo
        val iniciarPartida = { segundos: Int ->
            tiempoSeleccionadoSegundos = segundos
            tiempoRestanteSegundos = segundos
            lblContador.text = formatearTiempo(tiempoRestanteSegundos)
            lblContador.textFill = Color.web("#FFD700")
            lblContador.isVisible = true
            lblContador.isManaged = true

            modalTiempo.isVisible = false
            modalTiempo.isManaged = false

            timeline?.stop()
            timeline = Timeline(
                KeyFrame(Duration.seconds(1.0), {
                    if (tiempoRestanteSegundos > 0) {
                        tiempoRestanteSegundos--
                        lblContador.text = formatearTiempo(tiempoRestanteSegundos)
                    } else {
                        timeline?.stop()
                        lblContador.text = "00:00"
                        lblContador.textFill = Color.web("#FF4444")
                        pantallaDerrota.mostrar()
                    }
                })
            ).apply {
                cycleCount = Timeline.INDEFINITE
                play()
            }
        }

        btn3Min.setOnAction { iniciarPartida(180) }
        btn5Min.setOnAction { iniciarPartida(300) }
        btn10Min.setOnAction { iniciarPartida(600) }

        val filaBotonesTiempo = HBox(10.0).apply {
            alignment = Pos.CENTER
            children.addAll(btn3Min, btn5Min, btn10Min)
        }

        modalTiempo.children.addAll(tituloModal, filaBotonesTiempo)

        // Apilar: fondo, zona izquierda (volver + calculadora + respuesta t), zona derecha (reglas + datos), temporizador, modal de tiempo, reglas y derrota
        root.children.addAll(
            imageView,
            zonaIzquierda,
            zonaDerecha,
            lblContador,
            modalTiempo,
            cuadroReglas.contenedor,
            pantallaDerrota.contenedor
        )

        // Posicionar volver + calculadora + respuesta en la esquina superior izquierda
        StackPane.setAlignment(zonaIzquierda, Pos.TOP_LEFT)
        StackPane.setMargin(zonaIzquierda, Insets(15.0))

        // Posicionar REGLAS + datos en la esquina superior derecha
        StackPane.setAlignment(zonaDerecha, Pos.TOP_RIGHT)
        StackPane.setMargin(zonaDerecha, Insets(15.0))

        // Posicionar el temporizador en la esquina inferior izquierda
        StackPane.setAlignment(lblContador, Pos.BOTTOM_LEFT)
        StackPane.setMargin(lblContador, Insets(15.0))

        // El modal de tiempo, reglas y derrota se centran sobre la imagen
        StackPane.setAlignment(modalTiempo, Pos.CENTER)
        StackPane.setAlignment(cuadroReglas.contenedor, Pos.CENTER)
        StackPane.setAlignment(pantallaDerrota.contenedor, Pos.CENTER)

        return Scene(root, 400.0, 300.0)
    }

    /**
     * Genera valores enteros para a y d de modo que t = √(2 · d / a)
     * resulte en un valor entero limpio para el modo fácil.
     */
    private fun generarDatosPartida() {
        val tiempoExacto = listOf(2, 3, 4, 5, 6).random()
        val opcionesAceleracion = listOf(1, 2, 4).filter { a ->
            (a * tiempoExacto * tiempoExacto) % 2 == 0
        }
        val a = if (opcionesAceleracion.isNotEmpty()) opcionesAceleracion.random() else 2
        aceleracion = a.toDouble()
        distancia = (a * tiempoExacto * tiempoExacto / 2).toDouble()
        tiempoEsperado = validador.calcularTiempo(distancia, aceleracion)
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
