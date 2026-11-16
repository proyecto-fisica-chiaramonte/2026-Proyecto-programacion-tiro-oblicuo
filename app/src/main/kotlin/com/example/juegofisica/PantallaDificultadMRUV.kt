package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage

/**
 * Pantalla para seleccionar la dificultad del nivel MRUV (Fácil o Difícil) que extiende
 * [PantallaResultadoBase] para reutilizar la creación polimórfica de títulos y botones temáticos (OCP / DRY).
 */
class PantallaDificultadMRUV(
    private val stage: Stage,
    private val menuScene: Scene
) : PantallaResultadoBase(
    tituloTexto = "Selecciona la Dificultad",
    colorAcentoHex = "#FFFFFF"
), PantallaJuego {

    /**
     * Construye y devuelve la Scene de selección de dificultad.
     */
    override fun crearEscena(): Scene {
        val titulo = crearTextoDestacado(tituloTexto, 24.0, Color.WHITE)
        val contenedorDificultad = HBox(20.0, crearBotonModoFacil(), crearBotonModoDificil()).apply { alignment = Pos.CENTER }
        val btnVolver = componentesUI.crearBotonVolver { stage.scene = menuScene; stage.title = "Juego Educativo de Física" }.apply { text = "Volver atrás" }
        val root = VBox(25.0, titulo, contenedorDificultad, btnVolver).apply {
            alignment = Pos.CENTER; padding = Insets(40.0); style = "-fx-background-color: #000000;"
        }
        return Scene(root, 400.0, 300.0)
    }

    private fun crearBotonModoFacil(): Button {
        val btn = crearBotonTematico("Fácil", listOf("#005500", "#007700", "#00FF00", "#00FF00")) {
            stage.scene = PantallaMRUV(stage, menuScene).crearEscena()
            stage.title = "MRUV"
        }
        btn.prefWidth = 110.0
        return btn
    }

    private fun crearBotonModoDificil(): Button {
        val btn = crearBotonTematico("Difícil", listOf("#660000", "#880000", "#FF4444", "#FF4444")) {
            stage.scene = PantallaModoDificil(stage, menuScene).crearEscena()
            stage.title = "MRUV - Modo Difícil"
        }
        btn.prefWidth = 110.0
        return btn
    }
}
