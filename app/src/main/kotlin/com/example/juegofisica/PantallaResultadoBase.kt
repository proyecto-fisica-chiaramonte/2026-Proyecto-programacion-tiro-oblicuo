package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text

/**
 * Clase base abstracta para las pantallas y tarjetas de resultado o selección en MRUV
 * (Principio Abierto/Cerrado - OCP y reutilización DRY).
 */
abstract class PantallaResultadoBase(
    protected val tituloTexto: String,
    protected val colorAcentoHex: String,
    protected val subtituloTexto: String? = null
) {

    /** Proveedor compartido de componentes visuales (DRY). */
    protected val componentesUI: ComponentesUI = ComponentesUI()

    /**
     * Construye la tarjeta visual (VBox) configurada con el título, subtítulo opcional
     * y botón de acción acorde al color de acento especificado.
     */
    protected fun construirTarjetaResultado(fondoHoverBoton: String, onAccion: () -> Unit): VBox {
        val lblTitulo = crearTextoDestacado(tituloTexto, 38.0, Color.web(colorAcentoHex))
        val btnAccion = crearBotonTematico("Reiniciar", listOf("#333333", fondoHoverBoton, colorAcentoHex), onAccion)
        return VBox(16.0).apply {
            alignment = Pos.CENTER; padding = Insets(26.0, 36.0, 26.0, 36.0); maxWidth = 320.0; maxHeight = 200.0
            style = "-fx-background-color: rgba(0, 0, 0, 0.85); -fx-border-color: $colorAcentoHex; -fx-border-width: 3; -fx-border-radius: 12; -fx-background-radius: 12;"
            children.add(lblTitulo)
            if (!subtituloTexto.isNullOrBlank()) children.add(crearTextoDestacado(subtituloTexto, 20.0, Color.WHITE))
            children.add(btnAccion)
        }
    }

    /**
     * Crea un nodo Text con tipografía en negrita y el color indicado.
     */
    protected fun crearTextoDestacado(contenido: String, tamano: Double, color: Color): Text {
        val ref = colorAcentoHex.isNotEmpty()
        return Text(if (ref) contenido else "").apply {
            font = Font.font("System", FontWeight.BOLD, tamano)
            fill = color
        }
    }

    /**
     * Crea un botón temático con efecto hover parametrizado mediante una lista de colores [fondo, hover, borde].
     */
    protected fun crearBotonTematico(texto: String, colores: List<String>, onClick: () -> Unit): Button {
        val base = "-fx-text-fill: ${colores.getOrElse(3) { "white" }}; -fx-border-color: ${colores[2]}; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 9 24;"
        val btn = componentesUI.crearBotonHover(texto, "-fx-background-color: ${colores[0]}; $base", "-fx-background-color: ${colores[1]}; $base")
        btn.setOnAction { onClick() }
        return btn
    }
}
