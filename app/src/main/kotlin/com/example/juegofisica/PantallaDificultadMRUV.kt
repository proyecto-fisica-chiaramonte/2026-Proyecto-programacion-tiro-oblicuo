package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage

/**
 * Pantalla para seleccionar la dificultad del nivel MRUV (Fácil o Difícil).
 *
 * Presenta un fondo negro, un título centrado y opciones de dificultad,
 * además de la posibilidad de volver a la pantalla de explicación previa.
 *
 * @property stage el Stage principal de la aplicación.
 * @property menuScene la Scene del menú principal a la que se regresa.
 */
class PantallaDificultadMRUV(
    private val stage: Stage,
    private val menuScene: Scene
) {

    /**
     * Construye y devuelve la Scene de la pantalla de selección de dificultad.
     *
     * @return la Scene lista para asignarse al Stage principal.
     */
    fun crearEscena(): Scene {
        // Contenedor principal centrado con fondo negro
        val root = VBox(25.0).apply {
            alignment = Pos.CENTER
            padding = Insets(40.0)
            style = "-fx-background-color: #000000;"
        }

        // Título centrado en color blanco
        val titulo = Text("Selecciona la Dificultad").apply {
            font = Font.font("System", FontWeight.BOLD, 24.0)
            fill = Color.WHITE
        }

        // Contenedor horizontal para los dos botones de dificultad
        val contenedorDificultad = HBox(20.0).apply {
            alignment = Pos.CENTER
        }

        // Botón para seleccionar dificultad Fácil
        val btnFacil = Button("Fácil").apply {
            font = Font.font("System", FontWeight.BOLD, 14.0)
            prefWidth = 110.0
            style = "-fx-background-color: #005500; -fx-text-fill: #00FF00; " +
                    "-fx-border-color: #00FF00; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 10 20;"
            onMouseEntered = {
                style = "-fx-background-color: #007700; -fx-text-fill: #00FF00; " +
                        "-fx-border-color: #00FF00; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 10 20;"
            }
            onMouseExited = {
                style = "-fx-background-color: #005500; -fx-text-fill: #00FF00; " +
                        "-fx-border-color: #00FF00; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 10 20;"
            }
            setOnAction {
                val pantallaMRUV = PantallaMRUV(stage, menuScene)
                stage.scene = pantallaMRUV.crearEscena()
                stage.title = "MRUV"
            }
        }

        // Botón para seleccionar dificultad Difícil
        val btnDificil = Button("Difícil").apply {
            font = Font.font("System", FontWeight.BOLD, 14.0)
            prefWidth = 110.0
            style = "-fx-background-color: #660000; -fx-text-fill: #FF4444; " +
                    "-fx-border-color: #FF4444; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 10 20;"
            onMouseEntered = {
                style = "-fx-background-color: #880000; -fx-text-fill: #FF4444; " +
                        "-fx-border-color: #FF4444; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 10 20;"
            }
            onMouseExited = {
                style = "-fx-background-color: #660000; -fx-text-fill: #FF4444; " +
                        "-fx-border-color: #FF4444; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 10 20;"
            }
            setOnAction {
                val pantallaModoDificil = PantallaModoDificil(stage, menuScene)
                stage.scene = pantallaModoDificil.crearEscena()
                stage.title = "MRUV - Modo Difícil"
            }
        }

        contenedorDificultad.children.addAll(btnFacil, btnDificil)

        // Botón para volver atrás a la pantalla de explicación
        val btnVolver = Button("Volver atrás").apply {
            font = Font.font("System", FontWeight.MEDIUM, 14.0)
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
            setOnAction {
                val pantallaExplicacion = PantallaExplicacionMRUV(stage, menuScene)
                stage.scene = pantallaExplicacion.crearEscena()
                stage.title = "Instrucciones MRUV"
            }
        }

        // Agregar los componentes a la vista
        root.children.addAll(titulo, contenedorDificultad, btnVolver)

        return Scene(root, 400.0, 300.0)
    }
}
