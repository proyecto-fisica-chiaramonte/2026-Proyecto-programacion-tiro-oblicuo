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
 * Cuadro emergente que muestra la fórmula de MRUV sobre la pantalla principal.
 *
 * Se superpone centrado dentro del StackPane de la pantalla. Inicialmente
 * oculto, se muestra al presionar el botón "REGLAS" y se oculta al presionar
 * "Cerrar".
 *
 * Muestra únicamente la fórmula t = d / v en texto blanco sobre fondo negro.
 */
class CuadroReglas {

    /** VBox raíz del cuadro que contiene todo el contenido visual. */
    val contenedor: VBox = VBox(15.0).apply {
        alignment = Pos.CENTER
        padding = Insets(30.0)
        maxWidth = 220.0
        maxHeight = 140.0
        style = "-fx-background-color: #000000; " +
                "-fx-border-color: #555555; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10;"
        isVisible = false
        isManaged = false
    }

    /** Fórmula matemática de MRUV centrada y en texto blanco. */
    private val formula = Text("t = d / v").apply {
        font = Font.font("System", FontWeight.BOLD, 24.0)
        fill = Color.WHITE
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
        contenedor.children.addAll(formula, btnCerrar)
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
