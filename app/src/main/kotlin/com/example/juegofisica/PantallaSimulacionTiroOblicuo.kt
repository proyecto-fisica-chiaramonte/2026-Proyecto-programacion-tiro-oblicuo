package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import java.util.Locale

/**
 * Pantalla principal del módulo interactivo de Simulación de Tiro Oblicuo.
 *
 * Presenta un lienzo con fondo negro (`Color.BLACK`), eje X azul (`Color.BLUE`),
 * eje Y rojo (`Color.RED`) graduados de 10m en 10m con Auto-Zoom y una pelota blanca
 * (`Color.WHITE`) que parte siempre desde el origen cartesiano (0, 0), sube pasando por
 * la altura objetivo y_f y desciende hasta tocar tierra (y = 0) en x_f, junto con el panel
 * de 5 entradas ordenadas (v₀, g, x_f, y_f, θ) y los botones de control.
 *
 * Implementa la interfaz [PantallaJuego] cumpliendo con LSP y delega el cálculo y renderizado
 * en [CalculadorTiroOblicuo] y [CanvasSimulacionTiroOblicuo] (SRP).
 *
 * @property stage el Stage principal de la aplicación.
 * @property menuScene la Scene del menú principal a la que se regresa.
 */
class PantallaSimulacionTiroOblicuo(
    private val stage: Stage,
    private val menuScene: Scene
) : PantallaJuego {

    /** Proveedor reutilizable de componentes visuales (DRY). */
    private val componentesUI: ComponentesUI = ComponentesUI()

    /** Etiqueta de telemetría en vivo que muestra x(t), y(t), altura máxima alcanzada y tiempo t. */
    private val lblEstado: Label = Label("x = 0,0 m | y = 0,0 m | y_máx = 0,0 m | t = 0,00 s").apply {
        font = Font.font("System", FontWeight.BOLD, 10.5)
        textFill = Color.web("#FFD700")
    }

    /** Lienzo dedicado para renderizar los ejes cartesianos de 10m en 10m y la animación parabólica (SRP). */
    private val canvasSimulacion: CanvasSimulacionTiroOblicuo = CanvasSimulacionTiroOblicuo(
        onActualizarEstado = { x, y, yMax, t ->
            lblEstado.text = String.format(
                Locale.forLanguageTag("es-AR"),
                "x = %.1f m | y = %.1f m | y_máx = %.1f m | t = %.2f s",
                x,
                y,
                yMax,
                t
            )
        }
    )

    /** 1. Campo de entrada para la velocidad inicial v₀ (m/s). */
    private val txtVelocidadInicial: TextField = crearCampoParametro("30", "m/s")

    /** 2. Campo de entrada para la aceleración de la gravedad g (m/s², por defecto 9.8). */
    private val txtGravedad: TextField = crearCampoParametro("9.8", "9.8")

    /** 3. Campo de entrada para la posición final deseada X (x_f) (m). */
    private val txtPosicionFinalX: TextField = crearCampoParametro("100", "m")

    /** 4. Campo de entrada para la posición/altura deseada Y (y_f) (m). */
    private val txtPosicionFinalY: TextField = crearCampoParametro("20", "m")

    /** 5. Campo de entrada para el ángulo de lanzamiento θ (grados). */
    private val txtAngulo: TextField = crearCampoParametro("45", "grados")

    /**
     * Construye y devuelve la Scene completa de la simulación de Tiro Oblicuo.
     *
     * @return Scene configurada lista para asignarse al Stage principal.
     */
    override fun crearEscena(): Scene {
        val root = BorderPane().apply {
            style = "-fx-background-color: #000000;"
        }

        // Contenedor central para el Canvas de simulación con fondo negro
        val panelCanvas = Pane().apply {
            style = "-fx-background-color: #000000;"
            children.add(canvasSimulacion.canvas)
        }
        canvasSimulacion.vincularTamano(panelCanvas)

        // Panel lateral izquierdo con los controles de entrada y botones de acción
        val panelControles = construirPanelControles()

        // Barra inferior compacta con la lectura de coordenadas y tiempo en vivo
        val barraEstado = HBox(lblEstado).apply {
            alignment = Pos.CENTER
            padding = Insets(4.0, 8.0, 4.0, 8.0)
            style = "-fx-background-color: #0A0A0A; -fx-border-color: #222222; -fx-border-width: 1 0 0 0;"
        }

        root.left = panelControles
        root.center = panelCanvas
        root.bottom = barraEstado

        canvasSimulacion.restablecer()

        return Scene(root, 560.0, 350.0, Color.BLACK)
    }

    /**
     * Construye el panel lateral con los 5 campos de texto ordenados (v₀, g, x_f, y_f, θ)
     * y los botones de acción ("Lanzar", "Restablecer Parámetros" y "Volver al Menú").
     */
    private fun construirPanelControles(): VBox {
        val btnVolver = componentesUI.crearBotonVolver {
            canvasSimulacion.detener()
            stage.scene = menuScene
            stage.title = "Juego Educativo de Física"
        }

        val lblTitulo = Label("Tiro Oblicuo").apply {
            font = Font.font("System", FontWeight.BOLD, 14.0)
            textFill = Color.WHITE
        }

        val btnLanzar = Button("Lanzar").apply {
            maxWidth = Double.MAX_VALUE
            font = Font.font("System", FontWeight.BOLD, 12.0)
            style = "-fx-background-color: #005500; -fx-text-fill: #00FF00; " +
                    "-fx-border-color: #00FF00; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 5 10;"
            setOnAction { ejecutarSimulacion() }
        }

        val btnRestablecer = Button("Restablecer Parámetros").apply {
            maxWidth = Double.MAX_VALUE
            font = Font.font("System", FontWeight.BOLD, 10.5)
            style = "-fx-background-color: #262626; -fx-text-fill: #DDDDDD; " +
                    "-fx-border-color: #666666; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 5 8;"
            setOnAction { restablecerParametros() }
        }

        return VBox(5.0).apply {
            alignment = Pos.TOP_LEFT
            padding = Insets(8.0, 10.0, 8.0, 10.0)
            prefWidth = 178.0
            style = "-fx-background-color: #0D0D0D; -fx-border-color: #2A2A2A; -fx-border-width: 0 1 0 0;"
            children.addAll(
                btnVolver,
                lblTitulo,
                crearFilaParametro("v₀ (m/s):", txtVelocidadInicial),
                crearFilaParametro("g (m/s²):", txtGravedad),
                crearFilaParametro("x_f (m):", txtPosicionFinalX),
                crearFilaParametro("y_f (m):", txtPosicionFinalY),
                crearFilaParametro("θ (grados):", txtAngulo),
                btnLanzar,
                btnRestablecer
            )
        }
    }

    /**
     * Crea una fila compacta con etiqueta descriptiva y su campo numérico.
     */
    private fun crearFilaParametro(etiqueta: String, campo: TextField): HBox {
        val lbl = Label(etiqueta).apply {
            font = Font.font("System", FontWeight.BOLD, 11.0)
            textFill = Color.LIGHTGRAY
            prefWidth = 76.0
        }
        HBox.setHgrow(campo, Priority.ALWAYS)
        return HBox(4.0, lbl, campo).apply {
            alignment = Pos.CENTER_LEFT
        }
    }

    /**
     * Crea un TextField compacto con estilo oscuro para ingresar valores numéricos.
     */
    private fun crearCampoParametro(valorInicial: String, prompt: String): TextField {
        return TextField(valorInicial).apply {
            promptText = prompt
            prefWidth = 70.0
            font = Font.font("System", FontWeight.BOLD, 11.0)
            style = "-fx-background-color: #1A1A1A; -fx-text-fill: white; " +
                    "-fx-border-color: #555555; -fx-border-radius: 4; " +
                    "-fx-background-radius: 4; -fx-padding: 3 5;"
        }
    }

    /**
     * Lee los valores ingresados por el usuario e inicia la simulación del tiro parabólico
     * partiendo siempre desde (0, 0).
     */
    private fun ejecutarSimulacion() {
        val xf = leerValor(txtPosicionFinalX, 0.0).coerceAtLeast(0.0)
        val yf = leerValor(txtPosicionFinalY, 0.0).coerceAtLeast(0.0)
        val v0 = leerValor(txtVelocidadInicial, 0.0).coerceAtLeast(0.0)
        val g = leerValor(txtGravedad, 9.8).let { if (it > 0.0) it else 9.8 }
        val angulo = leerValor(txtAngulo, 45.0).coerceIn(1.0, 89.0)

        canvasSimulacion.simular(v0, g, xf, yf, angulo)
    }

    /**
     * Limpia los campos de entrada, restaura automáticamente la gravedad a 9.8 m/s²
     * y reinicia el lienzo de simulación en el origen (0, 0).
     */
    fun restablecerParametros() {
        txtVelocidadInicial.clear()
        txtGravedad.text = "9.8"
        txtPosicionFinalX.clear()
        txtPosicionFinalY.clear()
        txtAngulo.clear()
        canvasSimulacion.restablecer()
    }

    /**
     * Convierte el texto de un campo numérico a Double (soportando coma o punto decimal).
     */
    private fun leerValor(campo: TextField, porDefecto: Double): Double {
        val texto = (campo.text ?: "").trim().replace(',', '.')
        return texto.toDoubleOrNull() ?: porDefecto
    }
}
