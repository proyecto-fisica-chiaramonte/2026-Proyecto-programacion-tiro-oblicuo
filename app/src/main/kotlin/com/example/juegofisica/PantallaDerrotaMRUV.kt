package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text

/**
 * Overlay de derrota reutilizable para los modos fácil y difícil de MRUV (SRP / DRY).
 *
 * Se muestra centrado sobre la pantalla cuando el temporizador llega a 00:00 y reutiliza
 * [ComponentesUI] para construir el botón interactivo sin duplicar manejadores de hover.
 *
 * @param onReiniciar acción a ejecutar al presionar el botón "Reiniciar".
 * @param mensaje texto principal a mostrar en el aviso de derrota.
 */
class PantallaDerrotaMRUV(
    private val onReiniciar: () -> Unit,
    mensaje: String = "¡Perdiste!"
) {

    /** Proveedor compartido de componentes de UI (DRY). */
    private val componentesUI: ComponentesUI = ComponentesUI()

    /** VBox raíz del overlay de derrota. */
    val contenedor: VBox = VBox(20.0).apply {
        alignment = Pos.CENTER
        padding = Insets(30.0, 40.0, 30.0, 40.0)
        maxWidth = 320.0
        maxHeight = 180.0
        style = "-fx-background-color: rgba(0, 0, 0, 0.85); -fx-border-color: #FF4444; " +
                "-fx-border-width: 3; -fx-border-radius: 12; -fx-background-radius: 12;"
        isVisible = false
        isManaged = false
    }

    /** Mensaje destacado de derrota. */
    private val lblFallaste: Text = Text(mensaje).apply {
        font = Font.font("System", FontWeight.BOLD, 38.0)
        fill = Color.web("#FF4444")
    }

    /** Botón para reiniciar y volver al menú principal construido mediante ComponentesUI. */
    private val btnReiniciar: Button = componentesUI.crearBotonConHover(
        texto = "Reiniciar",
        estiloNormal = "-fx-background-color: #333333; -fx-text-fill: white; " +
                "-fx-border-color: #FF4444; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 28;",
        estiloHover = "-fx-background-color: #551111; -fx-text-fill: #FFAAAA; " +
                "-fx-border-color: #FF6666; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 28;",
        tamanoFuente = 16.0,
        pesoFuente = FontWeight.BOLD,
        onAccion = onReiniciar
    )

    init {
        contenedor.children.addAll(lblFallaste, btnReiniciar)
    }

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
