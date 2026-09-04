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
import javafx.stage.Stage

/**
 * Pantalla que se muestra al seleccionar "MRUV" desde el menú principal.
 *
 * Encapsula todo el estado y comportamiento de esta vista bajo el paradigma
 * de POO: las dependencias (Stage principal y escena del menú) se inyectan a
 * través del constructor primario, y la lógica de navegación se resuelve
 * internamente usando la referencia al Stage.
 *
 * @property stage el Stage principal de la aplicación, usado para
 *                 cambiar entre escenas y volver al menú.
 * @property menuScene la Scene del menú principal a la que se regresa al
 *                     presionar el botón "Volver atrás".
 */
class PantallaMRUV(
    private val stage: Stage,
    private val menuScene: Scene
) {

    /**
     * Construye y devuelve la Scene de la pantalla de MRUV.
     * Carga la imagen de fondo, la hace responsive y superpone el botón
     * "Volver atrás" en la esquina superior izquierda.
     *
     * @return la Scene lista para asignarse al Stage principal.
     */
    fun crearEscena(): Scene {
        // Contenedor raíz que ocupa toda la ventana y apila los elementos
        val root = StackPane()

        // Cargar la imagen de fondo desde los recursos del proyecto
        val imagenFondo = Image(javaClass.getResource("/images/fondo_mruv.jpg").toExternalForm())

        // ImageView que muestra la imagen ajustada al tamaño de la ventana
        val imageView = ImageView(imagenFondo).apply {
            isPreserveRatio = false
            // Vincular ancho y alto al tamaño del StackPane para ser responsive
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
            // Al hacer clic se restaura la escena del menú principal en el Stage
            setOnAction { stage.scene = menuScene }
        }

        // Apilar: primero la imagen de fondo y encima el botón
        root.children.addAll(imageView, btnVolver)

        // Posicionar el botón en la esquina superior izquierda con un margen
        StackPane.setAlignment(btnVolver, Pos.TOP_LEFT)
        StackPane.setMargin(btnVolver, Insets(15.0))

        return Scene(root, 400.0, 300.0)
    }
}