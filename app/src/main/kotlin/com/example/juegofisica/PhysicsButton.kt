package com.example.juegofisica

import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

/**
 * Botón personalizado para las opciones del menú de física.
 * Extiende Button de JavaFX y aplica un estilo visual consistente.
 * Sin eventos de clic por ahora (se pueden agregar luego).
 */
class PhysicsButton(texto: String) : Button(texto) {

    init {
        // Configuración visual del botón
        prefWidth = 250.0
        prefHeight = 50.0
        font = Font.font("System", FontWeight.MEDIUM, 16.0)
        styleClass.add("physics-button")

        // Efecto visual: cursor de mano al pasar por encima
        onMouseEntered = { cursor = Cursor.HAND }
        onMouseExited = { cursor = Cursor.DEFAULT }

        // Por ahora sin acción al hacer clic (se implementará después)
        // setOnAction { println("$texto seleccionado") }
    }
}
