package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

/**
 * Widget visual compacto de calculadora para la interfaz gráfica (SRP / DIP).
 *
 * Se encarga exclusivamente de la representación interactiva en JavaFX y delega
 * toda la lógica de cálculo a [ICalculadora].
 */
class CalculadoraWidget(
    private val motor: ICalculadora = MotorCalculadora()
) {

    /** Etiqueta del display visual de la calculadora. */
    private val lblDisplay: Label = Label(motor.display).apply {
        font = Font.font("System", FontWeight.BOLD, 14.0)
        textFill = Color.web("#FFD700")
        alignment = Pos.CENTER_RIGHT
        maxWidth = Double.MAX_VALUE
        style = "-fx-background-color: #111111; -fx-border-color: #555555; -fx-border-width: 1; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 6 8;"
    }

    /** Contenedor raíz privado del widget. */
    private val contenedor: VBox = VBox(6.0).apply {
        alignment = Pos.TOP_LEFT
        padding = Insets(8.0)
        prefWidth = 168.0
        maxWidth = 168.0
        style = "-fx-background-color: rgba(0, 0, 0, 0.80); -fx-border-color: #666666; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;"
    }

    init {
        val teclado = construirGrillaTeclado()
        contenedor.children.addAll(lblDisplay, teclado)
    }

    /**
     * Devuelve el contenedor visual VBox de la calculadora.
     */
    fun obtenerContenedor(): VBox = contenedor

    private fun construirGrillaTeclado(): GridPane {
        val grilla = GridPane().apply {
            hgap = 4.0; vgap = 4.0
            repeat(4) { columnConstraints.add(ColumnConstraints().apply { percentWidth = 25.0; hgrow = Priority.ALWAYS }) }
        }
        val filas = listOf(listOf("C", "√", "/", "*"), listOf("7", "8", "9", "-"), listOf("4", "5", "6", "+"), listOf("1", "2", "3", "="))
        filas.forEachIndexed { f, botones -> botones.forEachIndexed { c, txt -> grilla.add(crearBotonTecla(txt), c, f) } }
        grilla.add(crearBotonTecla("0"), 0, 4, 2, 1)
        grilla.add(crearBotonTecla("."), 2, 4, 2, 1)
        return grilla
    }

    private fun crearBotonTecla(simbolo: String): Button {
        val estilo = when {
            simbolo == "C" -> "-fx-background-color: #552222; -fx-text-fill: #FFAAAA; -fx-border-color: #FF6666;"
            simbolo in listOf("+", "-", "*", "/", "√", "=") -> "-fx-background-color: #2a2a00; -fx-text-fill: #FFD700; -fx-border-color: #FFD700;"
            else -> "-fx-background-color: #222222; -fx-text-fill: white; -fx-border-color: #666666;"
        }
        return Button(simbolo).apply {
            font = Font.font("System", FontWeight.BOLD, 12.0)
            prefHeight = 28.0
            minHeight = 28.0
            maxWidth = Double.MAX_VALUE
            style = "$estilo -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 2 4;"
            setOnAction { procesarPulsacion(simbolo) }
        }
    }

    private fun procesarPulsacion(simbolo: String) {
        lblDisplay.text = when (simbolo) {
            "C" -> motor.limpiar()
            "√" -> motor.aplicarRaiz()
            "=" -> motor.calcularResultado()
            "." -> motor.ingresarPunto()
            in listOf("+", "-", "*", "/") -> motor.establecerOperador(simbolo)
            else -> motor.ingresarDigito(simbolo)
        }
    }
}
