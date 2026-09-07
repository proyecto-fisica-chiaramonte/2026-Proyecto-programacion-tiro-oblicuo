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
import javafx.scene.text.TextAlignment
import javafx.stage.Stage

/**
 * Pantalla intermedia que muestra las instrucciones del nivel MRUV
 * antes de pasar a la pantalla del juego.
 *
 * Presenta una explicación del modo de juego y dos botones de acción:
 * volver al menú o comenzar la partida.
 *
 * @property stage el Stage principal de la aplicación.
 * @property menuScene la Scene del menú principal a la que se regresa.
 */
class PantallaExplicacionMRUV(
    private val stage: Stage,
    private val menuScene: Scene
) {

    /**
     * Construye y devuelve la Scene de la pantalla de explicación MRUV.
     *
     * @return la Scene lista para asignarse al Stage principal.
     */
    fun crearEscena(): Scene {
        // Contenedor principal con fondo negro que centra todo el contenido
        val root = VBox(20.0).apply {
            alignment = Pos.CENTER
            padding = Insets(40.0)
            style = "-fx-background-color: #000000;"
        }

        // Título del nivel
        val titulo = Text("Nivel MRUV").apply {
            font = Font.font("System", FontWeight.BOLD, 30.0)
            fill = Color.WHITE
        }

        // Texto explicativo del modo de juego
        val explicacion = Text().apply {
            text = "Este nivel consiste en aguantar lo más que puedas " +
                    "corriendo en una vía de tren, hasta que venga el tren. " +
                    "Este nivel lo podés repetir las veces que quieras " +
                    "y se juega de a 2, tipo competencia."
            font = Font.font("System", FontWeight.NORMAL, 15.0)
            fill = Color.LIGHTGRAY
            textAlignment = TextAlignment.CENTER
            wrappingWidth = 320.0
        }

        // Contenedor de botones alineados horizontalmente
        val contenedorBotones = HBox(20.0).apply {
            alignment = Pos.CENTER
            padding = Insets(10.0)
        }

        // Botón para volver al menú principal
        val btnVolverMenu = Button("Volver al Menú").apply {
            font = Font.font("System", FontWeight.MEDIUM, 14.0)
            style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                    "-fx-border-color: #666666; -fx-border-radius: 5; " +
                    "-fx-background-radius: 5; -fx-padding: 10 20;"
            onMouseEntered = {
                style = "-fx-background-color: #444444; -fx-text-fill: white; " +
                        "-fx-border-color: #888888; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 10 20;"
            }
            onMouseExited = {
                style = "-fx-background-color: #333333; -fx-text-fill: white; " +
                        "-fx-border-color: #666666; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 10 20;"
            }
            setOnAction { stage.scene = menuScene }
        }

        // Botón para comenzar el juego de MRUV
        val btnComenzar = Button("Comenzar").apply {
            font = Font.font("System", FontWeight.BOLD, 14.0)
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

        contenedorBotones.children.addAll(btnVolverMenu, btnComenzar)

        // Apilar todos los elementos en el contenedor principal
        root.children.addAll(titulo, explicacion, contenedorBotones)

        return Scene(root, 400.0, 300.0)
    }
}
