package com.example.juegofisica

import javafx.scene.layout.VBox

/**
 * Overlay de derrota reutilizable para los modos fácil y difícil de MRUV que extiende
 * [PantallaResultadoBase] cumpliendo con el Principio Abierto/Cerrado (OCP).
 */
class PantallaDerrotaMRUV(
    onReiniciar: () -> Unit,
    mensaje: String = "¡Perdiste!"
) : PantallaResultadoBase(
    tituloTexto = mensaje,
    colorAcentoHex = "#FF4444",
    subtituloTexto = null
) {

    /** VBox raíz privado del overlay de derrota construido por [PantallaResultadoBase]. */
    private val contenedor: VBox = construirTarjetaResultado("#551111", onReiniciar).apply {
        isVisible = false
        isManaged = false
    }

    /**
     * Devuelve el contenedor VBox del overlay de derrota.
     */
    fun obtenerContenedor(): VBox = contenedor

    /**
     * Muestra el overlay de derrota sobre la pantalla de juego.
     */
    fun mostrar() {
        contenedor.isVisible = true
        contenedor.isManaged = true
    }

    /**
     * Oculta el overlay de derrota.
     */
    fun ocultar() {
        contenedor.isVisible = false
        contenedor.isManaged = false
    }
}
