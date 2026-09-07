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
 * Utiliza una imagen JPG como fondo, superpone un botón "Volver atrás" en la
 * esquina superior izquierda y un botón "REGLAS" en la esquina superior derecha.
 * Al presionar "REGLAS" se muestra un cuadro emergente con las reglas de MRUV.
 *
 * @property stage el Stage principal de la aplicación, usado para volver al menú.
 * @property menuScene la Scene del menú principal a la que se regresa.
 */
class PantallaMRUV(
    private val stage: Stage,
    private val menuScene: Scene
) {

    /**
     * Construye y devuelve la Scene de la pantalla de MRUV con la imagen de
     * fondo, los botones de navegación y el cuadro de reglas integrado.
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

        // Botón para volver al menú principal (esquina superior izquierda)
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
            setOnAction { stage.scene = menuScene }
        }

        // Botón para mostrar reglas de MRUV (esquina superior derecha)
        val btnReglas = Button("REGLAS").apply {
            font = Font.font("System", FontWeight.BOLD, 14.0)
            style = "-fx-background-color: #1a1a1a; -fx-text-fill: #FFD700; " +
                    "-fx-border-color: #FFD700; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 8 16;"
            onMouseEntered = {
                style = "-fx-background-color: #2a2a00; -fx-text-fill: #FFD700; " +
                        "-fx-border-color: #FFAA00; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8 16;"
            }
            onMouseExited = {
                style = "-fx-background-color: #1a1a1a; -fx-text-fill: #FFD700; " +
                        "-fx-border-color: #FFD700; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8 16;"
            }
        }

        // Cuadro emergente de reglas (centrado, inicialmente oculto)
        val cuadroReglas = CuadroReglas()

        // Al hacer clic en REGLAS se muestra el cuadro de reglas
        btnReglas.setOnAction { cuadroReglas.mostrar() }

        // Apilar: imagen de fondo, botones y cuadro de reglas
        root.children.addAll(imageView, btnVolver, btnReglas, cuadroReglas.contenedor)

        // Posicionar botones y cuadro en sus esquinas respectivas
        StackPane.setAlignment(btnVolver, Pos.TOP_LEFT)
        StackPane.setMargin(btnVolver, Insets(15.0))

        StackPane.setAlignment(btnReglas, Pos.TOP_RIGHT)
        StackPane.setMargin(btnReglas, Insets(15.0))

        // El cuadro de reglas se centra sobre la imagen de fondo
        StackPane.setAlignment(cuadroReglas.contenedor, Pos.CENTER)

        return Scene(root, 400.0, 300.0)
    }
}
