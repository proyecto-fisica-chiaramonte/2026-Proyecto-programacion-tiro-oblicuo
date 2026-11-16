package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

/**
 * Componente dedicado a construir y gestionar el panel lateral de entradas de datos y botones
 * de control para la Simulación de Tiro Oblicuo (Principio de Responsabilidad Única - SRP).
 */
class PanelControlesTiroOblicuo(
    private val componentesUI: ComponentesUI = ComponentesUI(),
    private val onSimular: (ParametrosTiroOblicuo) -> Unit,
    private val onVolverMenu: () -> Unit
) {

    /** Lista ordenada de los 5 campos de texto: [v₀, g, x_f, y_f, θ]. */
    private val campos: List<TextField> = listOf(
        crearCampo("30", "m/s"),
        crearCampo("9.8", "9.8"),
        crearCampo("100", "m"),
        crearCampo("20", "m"),
        crearCampo("45", "grados")
    )

    /** Acción delegada opcional que se invoca al presionar "Restablecer Parámetros". */
    private var onRestablecerExtra: (() -> Unit)? = null

    /**
     * Registra el callback adicional para limpiar el lienzo al restablecer los parámetros.
     */
    fun vincularAccionRestablecer(accion: () -> Unit) {
        onRestablecerExtra = accion
    }

    /**
     * Construye y devuelve el contenedor VBox con las 5 filas de entrada y los botones de acción.
     */
    fun construirPanel(): VBox {
        val btnVolver = componentesUI.crearBotonVolver { onVolverMenu() }
        val lblTitulo = Label("Tiro Oblicuo").apply { font = Font.font("System", FontWeight.BOLD, 14.0); textFill = Color.WHITE }
        val etiquetas = listOf("v₀ (m/s):", "g (m/s²):", "x_f (m):", "y_f (m):", "θ (grados):")
        val filas = etiquetas.indices.map { i -> crearFila(etiquetas[i], campos[i]) }
        return VBox(5.0).apply {
            alignment = Pos.TOP_LEFT; padding = Insets(8.0, 10.0, 8.0, 10.0); prefWidth = 178.0
            style = "-fx-background-color: #0D0D0D; -fx-border-color: #2A2A2A; -fx-border-width: 0 1 0 0;"
            children.addAll(listOf(btnVolver, lblTitulo) + filas + listOf(crearBotonLanzar(), crearBotonRestablecer()))
        }
    }

    /**
     * Lee los 5 campos de entrada y construye el objeto de dominio [ParametrosTiroOblicuo].
     */
    fun leerParametros(): ParametrosTiroOblicuo {
        val v0 = leerDouble(0, 0.0)
        val g = leerDouble(1, 9.8).let { if (it > 0.0) it else 9.8 }
        val xf = leerDouble(2, 0.0)
        val yf = leerDouble(3, 0.0)
        val angulo = leerDouble(4, 45.0).coerceIn(1.0, 89.0)
        return ParametrosTiroOblicuo(v0, g, angulo).configurarObjetivo(xf, yf)
    }

    /**
     * Limpia las entradas, restaura automáticamente g = 9.8 m/s² y notifica el reinicio.
     */
    fun restablecerParametros() {
        campos.forEachIndexed { idx, campo -> campo.text = if (idx == 1) "9.8" else "" }
        onRestablecerExtra?.invoke()
    }

    private fun crearBotonLanzar(): Button {
        val estilo = "-fx-background-color: #005500; -fx-text-fill: #00FF00; " +
                "-fx-border-color: #00FF00; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 10;"
        return componentesUI.crearBotonSimple("Lanzar", estilo) { onSimular(leerParametros()) }
    }

    private fun crearBotonRestablecer(): Button {
        val estilo = "-fx-background-color: #262626; -fx-text-fill: #DDDDDD; " +
                "-fx-border-color: #666666; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 8;"
        return componentesUI.crearBotonSimple("Restablecer Parámetros", estilo) { restablecerParametros() }
    }

    private fun crearFila(etiqueta: String, campo: TextField): HBox {
        val lbl = componentesUI.crearEtiquetaFormulario(etiqueta)
        HBox.setHgrow(campo, Priority.ALWAYS)
        return HBox(4.0, lbl, campo).apply { alignment = Pos.CENTER_LEFT }
    }

    private fun crearCampo(valorInicial: String, prompt: String): TextField {
        return componentesUI.crearCampoFormulario(valorInicial, prompt)
    }

    private fun leerDouble(indice: Int, porDefecto: Double): Double {
        val texto = (campos[indice].text ?: "").trim().replace(',', '.')
        return texto.toDoubleOrNull() ?: porDefecto
    }
}
