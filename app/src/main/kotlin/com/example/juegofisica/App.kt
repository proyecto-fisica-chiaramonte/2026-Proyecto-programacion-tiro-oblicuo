package com.example.juegofisica

import javafx.application.Application

/**
 * Punto de entrada de la aplicación JavaFX.
 * Lanza la ventana de inicio del juego educativo de física.
 */
class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(InterfazDeInicio::class.java, *args)
        }
    }
}
