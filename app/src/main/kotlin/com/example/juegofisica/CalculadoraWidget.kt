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
 * Widget visual compacto de calculadora para la interfaz gráfica.
 *
 * Aplica el Principio de Responsabilidad Única (SRP) y de Inversión de Dependencias (DIP),
 * encargándose exclusivamente de la representación gráfica e interactiva en JavaFX y delegando
 * toda la lógica de cálculo a [ICalculadora].
 *
 * @param motor instancia del motor aritmético que realiza las operaciones matemáticas.
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
        style = "-fx-background-color: #111111; " +
                "-fx-border-color: #555555; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4; " +
                "-fx-padding: 6 8;"
    }

    /** Contenedor raíz del widget, listo para insertarse en la escena. */
    val contenedor: VBox = VBox(6.0).apply {
        alignment = Pos.TOP_LEFT
        padding = Insets(8.0)
        prefWidth = 168.0
        maxWidth = 168.0
        style = "-fx-background-color: rgba(0, 0, 0, 0.80); " +
                "-fx-border-color: #666666; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;"
    }

    init {
        val teclado = GridPane().apply {
            hgap = 4.0
            vgap = 4.0
            repeat(4) {
                columnConstraints.add(
                    ColumnConstraints().apply {
                        percentWidth = 25.0
                        hgrow = Priority.ALWAYS
                    }
                )
            }
        }

        // Fila 0: C, √, /, *
        teclado.add(crearBoton("C", true) { actualizarDisplay(motor.limpiar()) }, 0, 0)
        teclado.add(crearBoton("√") { actualizarDisplay(motor.aplicarRaiz()) }, 1, 0)
        teclado.add(crearBoton("/") { actualizarDisplay(motor.establecerOperador("/")) }, 2, 0)
        teclado.add(crearBoton("*") { actualizarDisplay(motor.establecerOperador("*")) }, 3, 0)

        // Fila 1: 7 8 9 -
        teclado.add(crearBoton("7") { actualizarDisplay(motor.ingresarDigito("7")) }, 0, 1)
        teclado.add(crearBoton("8") { actualizarDisplay(motor.ingresarDigito("8")) }, 1, 1)
        teclado.add(crearBoton("9") { actualizarDisplay(motor.ingresarDigito("9")) }, 2, 1)
        teclado.add(crearBoton("-") { actualizarDisplay(motor.establecerOperador("-")) }, 3, 1)

        // Fila 2: 4 5 6 +
        teclado.add(crearBoton("4") { actualizarDisplay(motor.ingresarDigito("4")) }, 0, 2)
        teclado.add(crearBoton("5") { actualizarDisplay(motor.ingresarDigito("5")) }, 1, 2)
        teclado.add(crearBoton("6") { actualizarDisplay(motor.ingresarDigito("6")) }, 2, 2)
        teclado.add(crearBoton("+") { actualizarDisplay(motor.establecerOperador("+")) }, 3, 2)

        // Fila 3: 1 2 3 =
        teclado.add(crearBoton("1") { actualizarDisplay(motor.ingresarDigito("1")) }, 0, 3)
        teclado.add(crearBoton("2") { actualizarDisplay(motor.ingresarDigito("2")) }, 1, 3)
        teclado.add(crearBoton("3") { actualizarDisplay(motor.ingresarDigito("3")) }, 2, 3)
        teclado.add(crearBoton("=") { actualizarDisplay(motor.calcularResultado()) }, 3, 3)

        // Fila 4: 0 (ocupa 2 columnas), punto decimal
        val btnCero = crearBoton("0") { actualizarDisplay(motor.ingresarDigito("0")) }
        teclado.add(btnCero, 0, 4, 2, 1)
        teclado.add(crearBoton(".") { actualizarDisplay(motor.ingresarPunto()) }, 2, 4, 2, 1)

        contenedor.children.addAll(lblDisplay, teclado)
    }

    /**
     * Actualiza el texto visible del display.
     */
    private fun actualizarDisplay(nuevoTexto: String) {
        lblDisplay.text = nuevoTexto
    }

    /**
     * Crea un botón compacto de la calculadora con los estilos visuales estándar.
     */
    private fun crearBoton(texto: String, esLimpieza: Boolean = false, accion: () -> Unit): Button {
        val colorFondo = when {
            esLimpieza -> "#552222"
            texto in listOf("+", "-", "*", "/", "√", "=") -> "#2a2a00"
            else -> "#222222"
        }
        val colorBorde = when {
            esLimpieza -> "#FF6666"
            texto in listOf("+", "-", "*", "/", "√", "=") -> "#FFD700"
            else -> "#666666"
        }
        val colorTexto = when {
            esLimpieza -> "#FFAAAA"
            texto in listOf("+", "-", "*", "/", "√", "=") -> "#FFD700"
            else -> "white"
        }

        return Button(texto).apply {
            font = Font.font("System", FontWeight.BOLD, 12.0)
            prefHeight = 28.0
            minHeight = 28.0
            maxWidth = Double.MAX_VALUE
            style = "-fx-background-color: $colorFondo; -fx-text-fill: $colorTexto; " +
                    "-fx-border-color: $colorBorde; -fx-border-radius: 4; " +
                    "-fx-background-radius: 4; -fx-padding: 2 4;"
            setOnAction { accion() }
        }
    }
}
