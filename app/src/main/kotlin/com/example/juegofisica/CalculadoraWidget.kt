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
 * Widget compacto de calculadora para resolver operaciones de MRUV.
 *
 * Incluye display, dígitos 0-9, punto decimal, operaciones básicas
 * (+, -, *, /), raíz cuadrada (√), limpieza (C) e igual (=).
 */
class CalculadoraWidget {

    /** Texto actual mostrado en el display. */
    private var textoDisplay: String = "0"

    /** Operando guardado al elegir una operación. */
    private var operandoAnterior: Double? = null

    /** Operador pendiente de aplicar (+, -, *, /). */
    private var operadorPendiente: String? = null

    /** Indica si el próximo dígito inicia un número nuevo. */
    private var iniciarNumeroNuevo: Boolean = true

    /** Etiqueta del display de la calculadora. */
    private val lblDisplay: Label = Label("0").apply {
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
            // Cuatro columnas de ancho uniforme
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
        teclado.add(crearBoton("C", true) { limpiar() }, 0, 0)
        teclado.add(crearBoton("√") { aplicarRaiz() }, 1, 0)
        teclado.add(crearBoton("/") { establecerOperador("/") }, 2, 0)
        teclado.add(crearBoton("*") { establecerOperador("*") }, 3, 0)

        // Fila 1: 7 8 9 -
        teclado.add(crearBoton("7") { ingresarDigito("7") }, 0, 1)
        teclado.add(crearBoton("8") { ingresarDigito("8") }, 1, 1)
        teclado.add(crearBoton("9") { ingresarDigito("9") }, 2, 1)
        teclado.add(crearBoton("-") { establecerOperador("-") }, 3, 1)

        // Fila 2: 4 5 6 +
        teclado.add(crearBoton("4") { ingresarDigito("4") }, 0, 2)
        teclado.add(crearBoton("5") { ingresarDigito("5") }, 1, 2)
        teclado.add(crearBoton("6") { ingresarDigito("6") }, 2, 2)
        teclado.add(crearBoton("+") { establecerOperador("+") }, 3, 2)

        // Fila 3: 1 2 3 =
        teclado.add(crearBoton("1") { ingresarDigito("1") }, 0, 3)
        teclado.add(crearBoton("2") { ingresarDigito("2") }, 1, 3)
        teclado.add(crearBoton("3") { ingresarDigito("3") }, 2, 3)
        teclado.add(crearBoton("=") { calcularResultado() }, 3, 3)

        // Fila 4: 0 (ocupa 2 columnas), punto decimal
        val btnCero = crearBoton("0") { ingresarDigito("0") }
        teclado.add(btnCero, 0, 4, 2, 1)
        teclado.add(crearBoton(".") { ingresarPunto() }, 2, 4, 2, 1)

        contenedor.children.addAll(lblDisplay, teclado)
    }

    /**
     * Crea un botón compacto de la calculadora con la acción indicada.
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

    /**
     * Actualiza el texto visible del display.
     */
    private fun actualizarDisplay() {
        lblDisplay.text = textoDisplay
    }

    /**
     * Ingresa un dígito en el display.
     */
    private fun ingresarDigito(digito: String) {
        if (iniciarNumeroNuevo || textoDisplay == "Error") {
            textoDisplay = digito
            iniciarNumeroNuevo = false
        } else if (textoDisplay == "0") {
            textoDisplay = digito
        } else {
            textoDisplay += digito
        }
        actualizarDisplay()
    }

    /**
     * Ingresa el punto decimal si aún no está presente.
     */
    private fun ingresarPunto() {
        if (iniciarNumeroNuevo || textoDisplay == "Error") {
            textoDisplay = "0."
            iniciarNumeroNuevo = false
        } else if (!textoDisplay.contains(".")) {
            textoDisplay += "."
        }
        actualizarDisplay()
    }

    /**
     * Guarda el operador pendiente y el operando actual.
     */
    private fun establecerOperador(operador: String) {
        if (textoDisplay == "Error") {
            limpiar()
            return
        }
        val valorActual = textoDisplay.toDoubleOrNull() ?: return
        if (operandoAnterior != null && operadorPendiente != null && !iniciarNumeroNuevo) {
            calcularResultado()
            if (textoDisplay == "Error") return
        }
        operandoAnterior = textoDisplay.toDoubleOrNull() ?: valorActual
        operadorPendiente = operador
        iniciarNumeroNuevo = true
    }

    /**
     * Aplica la raíz cuadrada al valor del display.
     */
    private fun aplicarRaiz() {
        val valor = textoDisplay.toDoubleOrNull()
        if (valor == null || valor < 0.0) {
            mostrarError()
            return
        }
        textoDisplay = formatearNumero(kotlin.math.sqrt(valor))
        iniciarNumeroNuevo = true
        actualizarDisplay()
    }

    /**
     * Evalúa la operación pendiente y muestra el resultado.
     */
    private fun calcularResultado() {
        val operador = operadorPendiente ?: return
        val izquierdo = operandoAnterior ?: return
        val derecho = textoDisplay.toDoubleOrNull() ?: return

        val resultado = when (operador) {
            "+" -> izquierdo + derecho
            "-" -> izquierdo - derecho
            "*" -> izquierdo * derecho
            "/" -> {
                if (derecho == 0.0) {
                    mostrarError()
                    return
                }
                izquierdo / derecho
            }
            else -> return
        }

        textoDisplay = formatearNumero(resultado)
        operandoAnterior = null
        operadorPendiente = null
        iniciarNumeroNuevo = true
        actualizarDisplay()
    }

    /**
     * Reinicia la calculadora al estado inicial.
     */
    private fun limpiar() {
        textoDisplay = "0"
        operandoAnterior = null
        operadorPendiente = null
        iniciarNumeroNuevo = true
        actualizarDisplay()
    }

    /**
     * Muestra un mensaje de error y limpia el estado de operación.
     */
    private fun mostrarError() {
        textoDisplay = "Error"
        operandoAnterior = null
        operadorPendiente = null
        iniciarNumeroNuevo = true
        actualizarDisplay()
    }

    /**
     * Formatea un número evitando decimales innecesarios.
     */
    private fun formatearNumero(valor: Double): String {
        return if (valor % 1.0 == 0.0 && valor in -1_000_000_000.0..1_000_000_000.0) {
            valor.toLong().toString()
        } else {
            String.format("%.6f", valor).trimEnd('0').trimEnd('.')
        }
    }
}
