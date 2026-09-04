package com.example.juegofisica

import javafx.geometry.Pos
import javafx.scene.layout.VBox

/**
 * Sección de menú que contiene los botones de navegación del juego.
 * Recibe lambdas de navegación que se ejecutan al hacer clic en cada botón.
 *
 * @param onTiroOblicuo función que se llama al hacer clic en "Tiro Oblicuo"
 * @param onMRUV función que se llama al hacer clic en "MRUV"
 */
class MenuButtonsSection(
    private val onTiroOblicuo: () -> Unit,
    private val onMRUV: () -> Unit
) {
    /**
     * Construye y devuelve un VBox con los botones del menú.
     * Cada botón ejecuta la lambda correspondiente al ser presionado.
     */
    fun construir(): VBox {
        return VBox(15.0).apply {
            alignment = Pos.CENTER
            maxWidth = 250.0

            val btnTiroOblicuo = PhysicsButton("Tiro Oblicuo").apply {
                setOnAction { onTiroOblicuo() }
            }

            val btnMRUV = PhysicsButton("MRUV").apply {
                setOnAction { onMRUV() }
            }

            children.addAll(btnTiroOblicuo, btnMRUV)
        }
    }
}
