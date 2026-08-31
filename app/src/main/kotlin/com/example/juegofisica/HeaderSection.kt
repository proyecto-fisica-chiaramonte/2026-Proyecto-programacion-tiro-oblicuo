package com.example.juegofisica

import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text

/**
 * Componente responsable de construir la sección de texto del encabezado.
 * Contiene el título principal "Bienvenido" y el subtítulo "elige 1".
 */
class HeaderSection {

    /**
     * Construye y retorna un VBox con los textos del encabezado.
     */
    fun construir(): VBox {
        return VBox(8.0).apply {
            alignment = Pos.CENTER

            // Texto principal: "Bienvenido" - estilo destacado, tamaño grande
            val titulo = Text("Bienvenido").apply {
                font = Font.font("System", FontWeight.BOLD, 36.0)
                styleClass.add("titulo-principal")
            }

            // Subtítulo: "elige 1" - ubicado justo debajo
            val subtitulo = Text("elige 1").apply {
                font = Font.font("System", FontWeight.NORMAL, 18.0)
                styleClass.add("subtitulo")
            }

            children.addAll(titulo, subtitulo)
        }
    }
}
