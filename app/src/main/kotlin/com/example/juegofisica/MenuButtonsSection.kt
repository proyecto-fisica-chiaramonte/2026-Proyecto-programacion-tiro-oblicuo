package com.example.juegofisica

import javafx.geometry.Pos
import javafx.scene.layout.VBox

/**
 * Sección de menú que contiene los botones de navegación del juego.
 * Recibe la lambda de navegación que se ejecuta al hacer clic en el botón de MRUV.
 *
 * @param onMRUV función que se llama al hacer clic en "MRUV"
 */
class MenuButtonsSection(
    private val onMRUV: () -> Unit
) {
    /**
     * Construye y devuelve un VBox con el botón del menú para ingresar a MRUV.
     */
    fun construir(): VBox {
        return VBox(15.0).apply {
            alignment = Pos.CENTER
            maxWidth = 250.0

            val btnMRUV = PhysicsButton("MRUV").apply {
                setOnAction { onMRUV() }
            }

            children.add(btnMRUV)
        }
    }
}
