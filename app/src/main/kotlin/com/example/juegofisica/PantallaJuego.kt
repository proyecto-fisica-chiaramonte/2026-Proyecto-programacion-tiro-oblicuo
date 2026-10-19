package com.example.juegofisica

import javafx.scene.Scene

/**
 * Contrato común para todas las pantallas navegables del juego educativo de física.
 *
 * Aplica el Principio de Abierto/Cerrado (OCP) y el Principio de Sustitución de Liskov (LSP),
 * permitiendo que cualquier pantalla pueda ser intercambiada y renderizada de manera uniforme.
 */
interface PantallaJuego {

    /**
     * Construye y devuelve la Scene de JavaFX configurada para esta pantalla.
     *
     * @return Scene lista para asignarse al Stage principal.
     */
    fun crearEscena(): Scene
}
