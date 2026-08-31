package com.example.juegofisica

import javafx.geometry.Pos
import javafx.scene.layout.VBox

/**
 * Componente responsable de construir y organizar el grupo de botones del menú.
 * Crea los botones para las opciones físicas usando la clase PhysicsButton.
 */
class MenuButtonsSection {

    /**
     * Construye y retorna un VBox con los botones de selección.
     */
    fun construir(): VBox {
        return VBox(15.0).apply {
            alignment = Pos.CENTER
            // Ancho fijo para que ambos botones tengan el mismo tamaño
            maxWidth = 250.0

            // Botón 1: "Tiro Oblicuo" - usa PhysicsButton personalizado
            val btnTiroOblicuo = PhysicsButton("Tiro Oblicuo")

            // Botón 2: "MRUV" - usa PhysicsButton personalizado
            val btnMRUV = PhysicsButton("MRUV")

            children.addAll(btnTiroOblicuo, btnMRUV)
        }
    }
}
