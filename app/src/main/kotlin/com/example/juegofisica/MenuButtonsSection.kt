package com.example.juegofisica

import javafx.geometry.Pos
import javafx.scene.layout.VBox

/**
 * Sección de menú que contiene los botones de navegación del juego.
 *
 * @param onMRUV función que se llama al hacer clic en "MRUV".
 * @param onTiroOblicuo función que se llama al hacer clic en "Tiro Oblicuo".
 */
class MenuButtonsSection(
    private val onMRUV: () -> Unit,
    private val onTiroOblicuo: () -> Unit = {}
) {
    /**
     * Construye y devuelve un VBox con los botones del menú para ingresar a MRUV y Tiro Oblicuo.
     */
    fun construir(): VBox {
        return VBox(15.0).apply {
            alignment = Pos.CENTER
            maxWidth = 250.0

            val btnMRUV = PhysicsButton("MRUV").apply {
                setOnAction { onMRUV() }
            }

            val btnTiroOblicuo = PhysicsButton("Tiro Oblicuo").apply {
                setOnAction { onTiroOblicuo() }
            }

            children.addAll(btnMRUV, btnTiroOblicuo)
        }
    }
}
