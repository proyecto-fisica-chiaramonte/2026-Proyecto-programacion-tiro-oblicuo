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
 * superpone un botón para volver al menú en la esquina superior izquierda,
 * un botón "REGLAS" en la esquina superior derecha, inicia automáticamente
 * la cuenta regresiva desde 2 minutos, muestra el texto del temporizador en
 * la esquina inferior izquierda y un cuadro emergente centrado con las
 * fórmulas principales del MRUV.
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

        // Cuadro emergente de reglas (centrado, inicialmente oculto)
        val cuadroReglas = VBox(12.0).apply {
            alignment = Pos.CENTER
            padding = Insets(20.0)
            maxWidth = 320.0
            maxHeight = 160.0
            style = "-fx-background-color: #000000; " +
                    "-fx-border-color: #555555; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10;"
            isVisible = false
            isManaged = false
        }

        // Fórmulas matemáticas de MRUV para el modo difícil
        val formula1 = Text("x(t) = x₀ + v₀ · t + ½ · a · t²").apply {
            font = Font.font("System", FontWeight.BOLD, 17.0)
            fill = Color.WHITE
        }

        val formula2 = Text("t = √(2 · d / a)").apply {
            font = Font.font("System", FontWeight.BOLD, 18.0)
            fill = Color.WHITE
        }

        // Botón para cerrar el cuadro de reglas
        val btnCerrar = Button("Cerrar").apply {
            font = Font.font("System", FontWeight.MEDIUM, 12.0)
            style = "-fx-background-color: #444444; -fx-text-fill: white; " +
                    "-fx-border-color: #777777; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 5 14;"
            onMouseEntered = {
                style = "-fx-background-color: #555555; -fx-text-fill: white; " +
                        "-fx-border-color: #999999; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 5 14;"
            }
            onMouseExited = {
                style = "-fx-background-color: #444444; -fx-text-fill: white; " +
                        "-fx-border-color: #777777; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 5 14;"
            }
            setOnAction {
                cuadroReglas.isVisible = false
                cuadroReglas.isManaged = false
            }
        }

        cuadroReglas.children.addAll(formula1, formula2, btnCerrar)

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
            cuadroReglas.isVisible = true
            cuadroReglas.isManaged = true
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

        // Apilar: imagen de fondo, botones, temporizador, reglas y derrota
        root.children.addAll(
            imageView,
            btnVolver,
            btnReglas,
            lblContador,
            cuadroReglas,
            pantallaDerrota.contenedor
        )

        // Posicionar el botón de volver en la esquina superior izquierda
        StackPane.setAlignment(btnVolver, Pos.TOP_LEFT)
        StackPane.setMargin(btnVolver, Insets(15.0))

        // Posicionar el botón de reglas en la esquina superior derecha
        StackPane.setAlignment(btnReglas, Pos.TOP_RIGHT)
        StackPane.setMargin(btnReglas, Insets(15.0))

        // Posicionar el temporizador en la esquina inferior izquierda
        StackPane.setAlignment(lblContador, Pos.BOTTOM_LEFT)
        StackPane.setMargin(lblContador, Insets(15.0))

        // El cuadro de reglas y derrota se centran sobre la imagen
        StackPane.setAlignment(cuadroReglas, Pos.CENTER)
        StackPane.setAlignment(pantallaDerrota.contenedor, Pos.CENTER)

        return Scene(root, 400.0, 300.0)
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
