package com.example.juegofisica

import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

/**
 * Botón personalizado para el juego educativo de física.
 * Tiene un estilo consistente y cambia el cursor al pasar el mouse.
 */
class PhysicsButton(texto: String) : Button(texto) {
    init {
        prefWidth = 250.0
        prefHeight = 50.0
        font = Font.font("System", FontWeight.MEDIUM, 16.0)
        styleClass.add("physics-button")
        onMouseEntered = { cursor = Cursor.HAND }
        onMouseExited = { cursor = Cursor.DEFAULT }
    }
}
