package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

/**
 * Cuadro emergente que muestra las reglas de MRUV sobre la pantalla principal.
 *
 * Se superpone centrado dentro del StackPane de la pantalla. Inicialmente
 * oculto, se muestra al presionar el botón "REGLAS" y se oculta al presionar
 * "Cerrar".
 *
 * Dimensiones máximas: 300x200 píxeles, con fondo negro, bordes redondeados
 * y texto blanco.
 */
class CuadroReglas {

    /** VBox raíz del cuadro que contiene todo el contenido visual. */
    val contenedor: VBox = VBox(10.0).apply {
        alignment = Pos.CENTER
        padding = Insets(20.0)
        maxWidth = 300.0
        maxHeight = 200.0
        style = "-fx-background-color: #000000; " +
                "-fx-border-color: #555555; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10;"
        isVisible = false
        isManaged = false
    }

    /** Texto explicativo con las reglas de MRUV. */
    private val textoReglas: TextFlow = TextFlow().apply {
        padding = Insets(5.0)
        children.add(Text().apply {
            text = "• MRUV = Movimiento Rectilíneo Uniformemente Variado\n\n" +
                    "• La aceleración es constante\n\n" +
                    "• La velocidad cambia de forma uniforme\n\n" +
                    "• Fórmula: Vf = Vi + a·t"
            fill = Color.WHITE
            font = Font.font("System", FontWeight.NORMAL, 13.0)
        })
    }

    /** Botón para cerrar el cuadro de reglas. */
    private val btnCerrar: Button = Button("Cerrar").apply {
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
        setOnAction { ocultar() }
    }

    init {
        // Título del cuadro
        val titulo = Text("REGLAS").apply {
            font = Font.font("System", FontWeight.BOLD, 18.0)
            fill = Color.WHITE
        }

        // Envolver el texto en un ScrollPane para que sea scrollable si no cabe
        val scrollPane = ScrollPane(textoReglas).apply {
            isFitToWidth = true
            prefHeight = 110.0
            style = "-fx-background: #000000; -fx-background-color: #000000; " +
                    "-fx-border-color: transparent; " +
                    "-fx-control-inner-background: #000000;"
        }

        contenedor.children.addAll(titulo, scrollPane, btnCerrar)
    }

    /**
     * Muestra el cuadro de reglas superpuesto en la pantalla.
     */
    fun mostrar() {
        contenedor.isVisible = true
        contenedor.isManaged = true
    }

    /**
     * Oculta el cuadro de reglas y muestra la pantalla limpia.
     */
    fun ocultar() {
        contenedor.isVisible = false
        contenedor.isManaged = false
    }
}
