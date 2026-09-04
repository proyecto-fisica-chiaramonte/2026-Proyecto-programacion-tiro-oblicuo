package com.example.juegofisica

import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text

/**
 * Sección del encabezado que contiene el título principal y el subtítulo.
 * Se muestra en la parte superior de la pantalla de inicio.
 */
class HeaderSection {
    /**
     * Construye y devuelve un VBox con el título y subtítulo del juego.
     */
    fun construir(): VBox {
        return VBox(8.0).apply {
            alignment = Pos.CENTER

            val titulo = Text("Bienvenido").apply {
                font = Font.font("System", FontWeight.BOLD, 36.0)
                styleClass.add("titulo-principal")
            }

            val subtitulo = Text("Elige que vas a jugar").apply {
                font = Font.font("System", FontWeight.NORMAL, 18.0)
                styleClass.add("subtitulo")
            }

            children.addAll(titulo, subtitulo)
        }
    }
}
