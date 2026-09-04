package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

/**
 * Pantalla que se muestra al seleccionar "MRUV" desde el menú principal.
 * Utiliza una imagen JPG como fondo y superpone un botón para volver al menú.
 *
 * @param onVolver función que se ejecuta al presionar "Volver atrás",
 *                 restaura la escena del menú principal en el Stage
 */
class PantallaMRUV(private val onVolver: () -> Unit) {

    /**
     * Crea y devuelve la Scene correspondiente a la pantalla de MRUV.
     * Carga la imagen de fondo y coloca el botón "Volver atrás" en la
     * esquina superior izquierda sobre la imagen.
     */
    fun crearEscena(): Scene {
        // Contenedor principal que ocupa toda la ventana
        val root = StackPane()

        // Cargar la imagen de fondo desde los recursos del proyecto
        val imagenFondo = Image(javaClass.getResource("/images/fondo_mruv.jpg").toExternalForm())

        // ImageView que muestra la imagen ajustada al tamaño de la ventana
        val imageView = ImageView(imagenFondo).apply {
            isPreserveRatio = false
            // Vincular el ancho y alto del ImageView al tamaño del StackPane
            fitWidthProperty().bind(root.widthProperty())
            fitHeightProperty().bind(root.heightProperty())
        }

        // Botón para volver al menú principal
        val btnVolver = Button("Volver atrás").apply {
            font = Font.font("System", FontWeight.NORMAL, 14.0)
            style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                    "-fx-border-color: #666666; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 8 16;"
            onMouseEntered = {
                style = "-fx-background-color: #444444; -fx-text-fill: white; " +
                        "-fx-border-color: #888888; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8 16;"
            }
            onMouseExited = {
                style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                        "-fx-border-color: #666666; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8 16;"
            }
            setOnAction { onVolver() }
        }

        // Apilar los elementos: la imagen de fondo primero y el botón encima
        root.children.addAll(imageView, btnVolver)

        // Posicionar el botón en la esquina superior izquierda con un margen
        StackPane.setAlignment(btnVolver, Pos.TOP_LEFT)
        StackPane.setMargin(btnVolver, Insets(15.0))

        return Scene(root, 400.0, 300.0)
    }
}
