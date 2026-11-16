package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text

/**
 * Cuadro emergente reutilizable que muestra las fórmulas sobre la pantalla principal (SRP / DRY).
 */
class CuadroReglas(
    vararg formulasTexto: String = arrayOf("x(t) = x₀ + v₀ · t + ½ · a · t²", "t = d / v")
) {

    /** Proveedor compartido de componentes de UI (DRY). */
    private val componentesUI: ComponentesUI = ComponentesUI()

    /** VBox raíz privado del cuadro que contiene las fórmulas y el botón de cierre. */
    private val contenedor: VBox = VBox(12.0).apply {
        alignment = Pos.CENTER
        padding = Insets(20.0)
        maxWidth = 320.0
        maxHeight = 160.0
        style = "-fx-background-color: #000000; -fx-border-color: #555555; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;"
        isVisible = false
        isManaged = false
    }

    init {
        for (formula in formulasTexto) {
            val tamano = if (formula.length > 25) 17.0 else 18.0
            contenedor.children.add(Text(formula).apply { font = Font.font("System", FontWeight.BOLD, tamano); fill = Color.WHITE })
        }
        val base = "-fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 14;"
        val btnCerrar = componentesUI.crearBotonHover("Cerrar", "-fx-background-color: #444444; -fx-border-color: #777777; $base", "-fx-background-color: #555555; -fx-border-color: #999999; $base")
        btnCerrar.setOnAction { ocultar() }
        contenedor.children.add(btnCerrar)
    }

    /**
     * Devuelve el contenedor visual VBox del cuadro de reglas.
     */
    fun obtenerContenedor(): VBox = contenedor

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
