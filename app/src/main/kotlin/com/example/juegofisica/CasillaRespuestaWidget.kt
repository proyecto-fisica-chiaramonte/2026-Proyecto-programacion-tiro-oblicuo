package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

/**
 * Componente visual compacto para ingresar y verificar la respuesta de la posición final x(t).
 *
 * Se ubica justo debajo de la calculadora en el lado izquierdo de la pantalla.
 * Incluye una etiqueta "X =", un campo de texto numérico reducido (~64px) y un botón "Verificar",
 * además de retroalimentación visual inmediata con cambio de bordes y aviso de resultado.
 *
 * @param onVerificar función delegada que valida el texto ingresado y devuelve true si es correcto.
 */
class CasillaRespuestaWidget(
    private val onVerificar: (String) -> Boolean,
    etiquetaTexto: String = "X ="
) {

    /** Etiqueta pequeña indicadora de la variable a calcular. */
    private val lblEtiqueta: Label = Label(etiquetaTexto).apply {
        font = Font.font("System", FontWeight.BOLD, 12.0)
        textFill = Color.web("#FFD700")
    }

    /** Campo de texto reducido para ingresar únicamente números. */
    private val txtRespuesta: TextField = TextField().apply {
        promptText = "0"
        prefWidth = 64.0
        maxWidth = 68.0
        prefHeight = 22.0
        minHeight = 22.0
        font = Font.font("System", FontWeight.BOLD, 11.0)
        style = "-fx-background-color: #111111; -fx-text-fill: white; " +
                "-fx-border-color: #666666; -fx-border-radius: 4; " +
                "-fx-background-radius: 4; -fx-padding: 2 4;"
        // Filtro para permitir únicamente caracteres numéricos, punto o coma y signo negativo
        textFormatter = TextFormatter<String> { change ->
            val nuevoTexto = change.controlNewText
            if (nuevoTexto.matches(Regex("^-?\\d*([.,]\\d*)?$"))) {
                change
            } else {
                null
            }
        }
        setOnAction { ejecutarVerificacion() }
    }

    /** Botón compacto para disparar la verificación del resultado ingresado. */
    private val btnVerificar: Button = Button("Verificar").apply {
        font = Font.font("System", FontWeight.BOLD, 10.0)
        prefHeight = 22.0
        minHeight = 22.0
        style = "-fx-background-color: #2a2a00; -fx-text-fill: #FFD700; " +
                "-fx-border-color: #FFD700; -fx-border-radius: 4; " +
                "-fx-background-radius: 4; -fx-padding: 2 6;"
        onMouseEntered = {
            if (lblMensaje.text != "¡CORRECTO!") {
                style = "-fx-background-color: #3d3d00; -fx-text-fill: #FFD700; " +
                        "-fx-border-color: #FFAA00; -fx-border-radius: 4; " +
                        "-fx-background-radius: 4; -fx-padding: 2 6;"
            }
        }
        onMouseExited = {
            if (lblMensaje.text != "¡CORRECTO!") {
                style = "-fx-background-color: #2a2a00; -fx-text-fill: #FFD700; " +
                        "-fx-border-color: #FFD700; -fx-border-radius: 4; " +
                        "-fx-background-radius: 4; -fx-padding: 2 6;"
            }
        }
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

    /** Contenedor raíz compacto alineado con el ancho de la calculadora (168px). */
    val contenedor: VBox = VBox(3.0).apply {
        alignment = Pos.CENTER
        padding = Insets(4.0, 6.0, 4.0, 6.0)
        prefWidth = 168.0
        maxWidth = 168.0
        style = "-fx-background-color: rgba(0, 0, 0, 0.85); " +
                "-fx-border-color: #666666; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6;"
    }

    init {
        val filaEntrada = HBox(4.0).apply {
            alignment = Pos.CENTER
            children.addAll(lblEtiqueta, txtRespuesta, btnVerificar)
        }
        contenedor.children.addAll(filaEntrada, lblMensaje)
    }

    /**
     * Ejecuta la validación delegada del texto ingresado y actualiza la retroalimentación visual.
     */
    fun ejecutarVerificacion() {
        val entrada = txtRespuesta.text ?: ""
        if (entrada.isBlank()) {
            mostrarIncorrecto("INCORRECTO")
            return
        }
        val esCorrecto = onVerificar(entrada)
        if (esCorrecto) {
            mostrarCorrecto("¡CORRECTO!")
        } else {
            mostrarIncorrecto("INCORRECTO")
        }
    }

    /**
     * Muestra mensaje visual positivo con bordes verdes en el contenedor, campo y botón.
     */
    fun mostrarCorrecto(mensaje: String = "¡CORRECTO!") {
        lblMensaje.text = mensaje
        lblMensaje.textFill = Color.web("#00FF00")
        contenedor.style = "-fx-background-color: rgba(0, 0, 0, 0.85); " +
                "-fx-border-color: #00FF00; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6;"
        txtRespuesta.style = "-fx-background-color: #111111; -fx-text-fill: #00FF00; " +
                "-fx-border-color: #00FF00; -fx-border-radius: 4; " +
                "-fx-background-radius: 4; -fx-padding: 2 4;"
        btnVerificar.style = "-fx-background-color: #003300; -fx-text-fill: #00FF00; " +
                "-fx-border-color: #00FF00; -fx-border-radius: 4; " +
                "-fx-background-radius: 4; -fx-padding: 2 6;"
    }

    /**
     * Muestra aviso de error con bordes rojos en el contenedor, campo y botón.
     */
    fun mostrarIncorrecto(mensaje: String = "INCORRECTO") {
        lblMensaje.text = mensaje
        lblMensaje.textFill = Color.web("#FF4444")
        contenedor.style = "-fx-background-color: rgba(0, 0, 0, 0.85); " +
                "-fx-border-color: #FF4444; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6;"
        txtRespuesta.style = "-fx-background-color: #111111; -fx-text-fill: #FF6666; " +
                "-fx-border-color: #FF4444; -fx-border-radius: 4; " +
                "-fx-background-radius: 4; -fx-padding: 2 4;"
        btnVerificar.style = "-fx-background-color: #330000; -fx-text-fill: #FF8888; " +
                "-fx-border-color: #FF4444; -fx-border-radius: 4; " +
                "-fx-background-radius: 4; -fx-padding: 2 6;"
    }

    /**
     * Obtiene el texto actual ingresado en la casilla de respuesta.
     */
    fun obtenerTexto(): String = txtRespuesta.text ?: ""

    /**
     * Establece el texto ingresado en la casilla de respuesta.
     */
    fun establecerTexto(texto: String) {
        txtRespuesta.text = texto
    }

    /**
     * Deshabilita la entrada de texto y el botón de verificación al terminar la partida.
     */
    fun deshabilitar() {
        txtRespuesta.isDisable = true
        btnVerificar.isDisable = true
    }

    /**
     * Restablece la casilla de respuesta a su apariencia inicial limpia.
     */
    fun reiniciar() {
        txtRespuesta.isDisable = false
        btnVerificar.isDisable = false
        txtRespuesta.text = ""
        lblMensaje.text = ""
        contenedor.style = "-fx-background-color: rgba(0, 0, 0, 0.85); " +
                "-fx-border-color: #666666; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6;"
        txtRespuesta.style = "-fx-background-color: #111111; -fx-text-fill: white; " +
                "-fx-border-color: #666666; -fx-border-radius: 4; " +
                "-fx-background-radius: 4; -fx-padding: 2 4;"
        btnVerificar.style = "-fx-background-color: #2a2a00; -fx-text-fill: #FFD700; " +
                "-fx-border-color: #FFD700; -fx-border-radius: 4; " +
                "-fx-background-radius: 4; -fx-padding: 2 6;"
    }
}
