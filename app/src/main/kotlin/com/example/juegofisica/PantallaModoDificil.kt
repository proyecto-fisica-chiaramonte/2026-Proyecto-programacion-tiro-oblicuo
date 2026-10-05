package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage

/**
 * Pantalla correspondiente al modo difícil del nivel MRUV.
 *
 * Utiliza una imagen JPG como fondo que se adapta al tamaño de la ventana,
 * superpone un botón para volver al menú en la esquina superior izquierda,
 * un botón "REGLAS" en la esquina superior derecha y un cuadro emergente
 * centrado con las fórmulas principales del MRUV.
 *
 * @property stage el Stage principal de la aplicación, usado para volver al menú.
 * @property menuScene la Scene del menú principal a la que se regresa.
 */
class PantallaModoDificil(
    private val stage: Stage,
    private val menuScene: Scene
) {

    /**
     * Construye y devuelve la Scene de la pantalla de modo difícil con la imagen de
     * fondo, los botones de navegación y el cuadro de reglas integrado.
     *
     * @return la Scene lista para asignarse al Stage principal.
     */
    fun crearEscena(): Scene {
        // Contenedor raíz que ocupa toda la ventana y apila los elementos
        val root = StackPane()

        // Cargar la imagen de fondo desde los recursos del proyecto
        val imagenFondo = Image(javaClass.getResource("/images/fondo_mruv_d.jpg").toExternalForm())

        // ImageView que muestra la imagen ajustada al tamaño de la ventana
        val imageView = ImageView(imagenFondo).apply {
            isPreserveRatio = false
            // Vincular ancho y alto al tamaño del StackPane para ser responsive
            fitWidthProperty().bind(root.widthProperty())
            fitHeightProperty().bind(root.heightProperty())
        }

        // Botón para volver al menú principal (esquina superior izquierda)
        val btnVolver = Button("Volver al Menú").apply {
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

        // Botón para mostrar reglas del modo difícil (esquina superior derecha)
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
        val cuadroReglas = VBox(12.0).apply {
            alignment = Pos.CENTER
            padding = Insets(20.0)
            maxWidth = 320.0
            maxHeight = 160.0
            style = "-fx-background-color: #000000; " +
                    "-fx-border-color: #555555; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10;"
            isVisible = false
            isManaged = false
        }

        // Fórmulas matemáticas de MRUV para el modo difícil
        val formula1 = Text("x(t) = x₀ + v₀ · t + ½ · a · t²").apply {
            font = Font.font("System", FontWeight.BOLD, 17.0)
            fill = Color.WHITE
        }

        val formula2 = Text("t = √(2 · d / a)").apply {
            font = Font.font("System", FontWeight.BOLD, 18.0)
            fill = Color.WHITE
        }

        // Botón para cerrar el cuadro de reglas
        val btnCerrar = Button("Cerrar").apply {
            font = Font.font("System", FontWeight.MEDIUM, 12.0)
            style = "-fx-background-color: #444444; -fx-text-fill: white; " +
                    "-fx-border-color: #777777; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 5 14;"
            onMouseEntered = {
                style = "-fx-background-color: #555555; -fx-text-fill: white; " +
                        "-fx-border-color: #999999; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 5 14;"
            }
            onMouseExited = {
                style = "-fx-background-color: #444444; -fx-text-fill: white; " +
                        "-fx-border-color: #777777; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 5 14;"
            }
            setOnAction {
                cuadroReglas.isVisible = false
                cuadroReglas.isManaged = false
            }
        }

        cuadroReglas.children.addAll(formula1, formula2, btnCerrar)

        // Al hacer clic en REGLAS se muestra el cuadro de reglas
        btnReglas.setOnAction {
            cuadroReglas.isVisible = true
            cuadroReglas.isManaged = true
        }

        // Apilar la imagen de fondo, botones y el cuadro de reglas
        root.children.addAll(imageView, btnVolver, btnReglas, cuadroReglas)

        // Posicionar el botón de volver en la esquina superior izquierda
        StackPane.setAlignment(btnVolver, Pos.TOP_LEFT)
        StackPane.setMargin(btnVolver, Insets(15.0))

        // Posicionar el botón de reglas en la esquina superior derecha
        StackPane.setAlignment(btnReglas, Pos.TOP_RIGHT)
        StackPane.setMargin(btnReglas, Insets(15.0))

        // El cuadro de reglas se centra sobre la imagen de fondo
        StackPane.setAlignment(cuadroReglas, Pos.CENTER)

        return Scene(root, 400.0, 300.0)
    }
}
