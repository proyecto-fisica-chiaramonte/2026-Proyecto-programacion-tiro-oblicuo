package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

/**
 * Componente visual compacto para ingresar y verificar la respuesta en los niveles de MRUV (SRP / DRY).
 */
class CasillaRespuestaWidget(
    private val onVerificar: (String) -> Boolean,
    etiquetaTexto: String = "X ="
) {

    /** Campo de texto reducido para ingresar únicamente números. */
    private val txtRespuesta: TextField = TextField().apply {
        promptText = "0"
        prefWidth = 64.0
        maxWidth = 68.0
        prefHeight = 22.0
        minHeight = 22.0
        font = Font.font("System", FontWeight.BOLD, 11.0)
        textFormatter = TextFormatter<String> { change ->
            if (change.controlNewText.matches(Regex("^-?\\d*([.,]\\d*)?$"))) change else null
        }
        setOnAction { ejecutarVerificacion() }
    }

    /** Botón compacto para disparar la verificación del resultado ingresado. */
    private val btnVerificar: Button = Button("Verificar").apply {
        font = Font.font("System", FontWeight.BOLD, 10.0)
        prefHeight = 22.0
        minHeight = 22.0
        setOnAction { ejecutarVerificacion() }
    }

    /** Etiqueta pequeña de retroalimentación para mostrar éxito o error al jugador. */
    private val lblMensaje: Label = Label("").apply {
        font = Font.font("System", FontWeight.BOLD, 10.0)
        alignment = Pos.CENTER
        maxWidth = Double.MAX_VALUE
        prefHeight = 13.0
        minHeight = 13.0
    }

    /** Contenedor raíz privado alineado con el ancho de la calculadora (168px). */
    private val contenedor: VBox = VBox(3.0).apply {
        alignment = Pos.CENTER
        padding = Insets(4.0, 6.0, 4.0, 6.0)
        prefWidth = 168.0
        maxWidth = 168.0
    }

    init {
        val lblEtiqueta = Label(etiquetaTexto).apply {
            font = Font.font("System", FontWeight.BOLD, 12.0)
            textFill = Color.web("#FFD700")
        }
        val filaEntrada = HBox(4.0, lblEtiqueta, txtRespuesta, btnVerificar).apply { alignment = Pos.CENTER }
        contenedor.children.addAll(filaEntrada, lblMensaje)
        aplicarTemaVisual(null)
    }

    /** Devuelve el contenedor visual VBox de la casilla de respuesta. */
    fun obtenerContenedor(): VBox = contenedor

    /** Ejecuta la validación delegada del texto ingresado y actualiza la retroalimentación visual. */
    fun ejecutarVerificacion() {
        val entrada = txtRespuesta.text ?: ""
        if (entrada.isBlank() || !onVerificar(entrada)) {
            mostrarIncorrecto("INCORRECTO")
        } else {
            mostrarCorrecto("¡CORRECTO!")
        }
    }

    /** Muestra mensaje visual positivo con bordes verdes. */
    fun mostrarCorrecto(mensaje: String = "¡CORRECTO!") {
        lblMensaje.text = mensaje
        lblMensaje.textFill = Color.web("#00FF00")
        aplicarTemaVisual(true)
    }

    /** Muestra aviso de error con bordes rojos. */
    fun mostrarIncorrecto(mensaje: String = "INCORRECTO") {
        lblMensaje.text = mensaje
        lblMensaje.textFill = Color.web("#FF4444")
        aplicarTemaVisual(false)
    }

    /** Obtiene el texto actual ingresado en la casilla de respuesta. */
    fun obtenerTexto(): String = txtRespuesta.text ?: ""

    /** Establece el texto ingresado en la casilla de respuesta. */
    fun establecerTexto(texto: String) {
        txtRespuesta.text = texto
    }

    /** Deshabilita la entrada de texto y el botón de verificación al terminar la partida. */
    fun deshabilitar() {
        txtRespuesta.isDisable = true
        btnVerificar.isDisable = true
    }

    /** Restablece la casilla de respuesta a su apariencia inicial limpia. */
    fun reiniciar() {
        txtRespuesta.isDisable = false
        btnVerificar.isDisable = false
        txtRespuesta.text = ""
        lblMensaje.text = ""
        aplicarTemaVisual(null)
    }

    private fun aplicarTemaVisual(esExito: Boolean?) {
        val borde = when (esExito) { true -> "#00FF00"; false -> "#FF4444"; null -> "#666666" }
        val ancho = if (esExito == null) 1 else 2
        val txtColor = when (esExito) { true -> "#00FF00"; false -> "#FF6666"; null -> "white" }
        val bgBtn = when (esExito) { true -> "#003300"; false -> "#330000"; null -> "#2a2a00" }
        val txtBtn = when (esExito) { true -> "#00FF00"; false -> "#FF8888"; null -> "#FFD700" }
        val brdBtn = if (esExito == null) "#FFD700" else borde
        contenedor.style = "-fx-background-color: rgba(0, 0, 0, 0.85); -fx-border-color: $borde; -fx-border-width: $ancho; -fx-border-radius: 6; -fx-background-radius: 6;"
        txtRespuesta.style = "-fx-background-color: #111111; -fx-text-fill: $txtColor; -fx-border-color: $borde; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 2 4;"
        btnVerificar.style = "-fx-background-color: $bgBtn; -fx-text-fill: $txtBtn; -fx-border-color: $brdBtn; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 2 6;"
    }
}
