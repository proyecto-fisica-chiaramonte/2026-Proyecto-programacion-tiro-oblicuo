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
 *
 * Incluye una etiqueta indicadora ("X =" o "t ="), un campo numérico filtrado y un botón
 * "Verificar" con retroalimentación visual unificada mediante [aplicarTemaVisual].
 *
 * @param onVerificar función delegada que valida el texto ingresado y devuelve true si es correcto.
 * @param etiquetaTexto texto identificador de la incógnita a responder.
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
    private val txtRespuesta: TextField = crearCampoNumerico()

    /** Botón compacto para disparar la verificación del resultado ingresado. */
    private val btnVerificar: Button = crearBotonVerificar()

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
    }

    init {
        val filaEntrada = HBox(4.0, lblEtiqueta, txtRespuesta, btnVerificar).apply {
            alignment = Pos.CENTER
        }
        contenedor.children.addAll(filaEntrada, lblMensaje)
        aplicarTemaVisual("#666666", 1, "white", "#2a2a00", "#FFD700", "#FFD700")
    }

    /**
     * Ejecuta la validación delegada del texto ingresado y actualiza la retroalimentación visual.
     */
    fun ejecutarVerificacion() {
        val entrada = txtRespuesta.text ?: ""
        if (entrada.isBlank() || !onVerificar(entrada)) {
            mostrarIncorrecto("INCORRECTO")
        } else {
            mostrarCorrecto("¡CORRECTO!")
        }
    }

    /**
     * Muestra mensaje visual positivo con bordes verdes.
     */
    fun mostrarCorrecto(mensaje: String = "¡CORRECTO!") {
        lblMensaje.text = mensaje
        lblMensaje.textFill = Color.web("#00FF00")
        aplicarTemaVisual("#00FF00", 2, "#00FF00", "#003300", "#00FF00", "#00FF00")
    }

    /**
     * Muestra aviso de error con bordes rojos.
     */
    fun mostrarIncorrecto(mensaje: String = "INCORRECTO") {
        lblMensaje.text = mensaje
        lblMensaje.textFill = Color.web("#FF4444")
        aplicarTemaVisual("#FF4444", 2, "#FF6666", "#330000", "#FF8888", "#FF4444")
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
        aplicarTemaVisual("#666666", 1, "white", "#2a2a00", "#FFD700", "#FFD700")
    }

    /**
     * Construye el campo de texto con filtro numérico decimal.
     */
    private fun crearCampoNumerico(): TextField {
        return TextField().apply {
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
    }

    /**
     * Construye el botón "Verificar" con su acción asociada.
     */
    private fun crearBotonVerificar(): Button {
        return Button("Verificar").apply {
            font = Font.font("System", FontWeight.BOLD, 10.0)
            prefHeight = 22.0
            minHeight = 22.0
            setOnAction { ejecutarVerificacion() }
        }
    }

    /**
     * Centraliza la actualización de estilos CSS del contenedor, campo de texto y botón (DRY).
     */
    private fun aplicarTemaVisual(
        colorBorde: String,
        anchoBorde: Int,
        colorTextoCampo: String,
        fondoBoton: String,
        textoBoton: String,
        bordeBoton: String
    ) {
        contenedor.style = "-fx-background-color: rgba(0, 0, 0, 0.85); -fx-border-color: $colorBorde; " +
                "-fx-border-width: $anchoBorde; -fx-border-radius: 6; -fx-background-radius: 6;"
        txtRespuesta.style = "-fx-background-color: #111111; -fx-text-fill: $colorTextoCampo; " +
                "-fx-border-color: $colorBorde; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 2 4;"
        btnVerificar.style = "-fx-background-color: $fondoBoton; -fx-text-fill: $textoBoton; " +
                "-fx-border-color: $bordeBoton; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 2 6;"
    }
}
