package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment

/**
 * Fábrica y proveedor de componentes visuales compartidos de la interfaz de usuario (DRY / SRP).
 *
 * Centraliza la creación y estilos de botones, etiquetas, campos numéricos y paneles de datos
 * manteniendo métodos cortos de hasta 3 parámetros y cohesión total.
 */
class ComponentesUI(
    private val fuenteFamilia: String = "System"
) {

    /**
     * Crea un botón interactivo con cambio de estilo al pasar el cursor (hover).
     */
    fun crearBotonHover(texto: String, estiloNormal: String, estiloHover: String): Button {
        return Button(texto).apply {
            font = Font.font(fuenteFamilia, FontWeight.BOLD, 14.0)
            style = estiloNormal
            onMouseEntered = { style = estiloHover }
            onMouseExited = { style = estiloNormal }
        }
    }

    /**
     * Crea un botón compacto de ancho completo con acción directa.
     */
    fun crearBotonSimple(texto: String, estilo: String, onAccion: () -> Unit): Button {
        return Button(texto).apply {
            maxWidth = Double.MAX_VALUE
            font = Font.font(fuenteFamilia, FontWeight.BOLD, 11.0)
            style = estilo
            setOnAction { onAccion() }
        }
    }

    /**
     * Construye el botón estándar "Volver al Menú" con sus efectos de hover y acción asignada.
     */
    fun crearBotonVolver(onVolver: () -> Unit): Button {
        val base = "-fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 16;"
        val btn = crearBotonHover("Volver al Menú", "-fx-background-color: #333333; -fx-border-color: #666666; $base", "-fx-background-color: #444444; -fx-border-color: #888888; $base")
        btn.font = Font.font(fuenteFamilia, FontWeight.NORMAL, 14.0)
        btn.setOnAction { onVolver() }
        return btn
    }

    /**
     * Construye el botón estándar "REGLAS" con acento dorado y efecto hover.
     */
    fun crearBotonReglas(onMostrarReglas: () -> Unit): Button {
        val base = "-fx-text-fill: #FFD700; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 16;"
        val btn = crearBotonHover("REGLAS", "-fx-background-color: #1a1a1a; -fx-border-color: #FFD700; $base", "-fx-background-color: #2a2a00; -fx-border-color: #FFAA00; $base")
        btn.setOnAction { onMostrarReglas() }
        return btn
    }

    /**
     * Crea una etiqueta formateada en tipografía dorada para mostrar variables físicas.
     */
    fun crearEtiquetaDato(texto: String): Label {
        return Label(texto).apply {
            font = Font.font(fuenteFamilia, FontWeight.BOLD, 13.0)
            textFill = Color.web("#FFD700")
        }
    }

    /**
     * Crea un panel contenedor translúcido alineado a la derecha con borde dorado para agrupar datos.
     */
    fun crearPanelDatos(textosDatos: List<String>): VBox {
        val etiquetas = textosDatos.map { crearEtiquetaDato(it) }
        return VBox(4.0).apply {
            alignment = Pos.TOP_RIGHT
            padding = Insets(8.0, 10.0, 8.0, 10.0)
            maxWidth = Region.USE_PREF_SIZE
            style = "-fx-background-color: rgba(0, 0, 0, 0.75); -fx-border-color: #FFD700; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;"
            children.addAll(etiquetas)
        }
    }

    /**
     * Crea el contenedor destacado superior con el mensaje de atención para las pantallas MRUV.
     */
    fun crearCartelAtencion(): Label {
        val msj = "ATENCION: Este juego consiste en hacer la cuenta matematica correspondiente a la dificultad en menos de un minuto, la formula se encuentra en el apartado reglas dentro del nivel, los datos para resolver esa cuanta estan debajo del apartado reglas"
        return Label(msj).apply {
            isWrapText = true
            maxWidth = 215.0
            maxHeight = Region.USE_PREF_SIZE
            padding = Insets(6.0, 10.0, 6.0, 10.0)
            font = Font.font(fuenteFamilia, FontWeight.BOLD, 10.0)
            textFill = Color.web("#FFD700")
            textAlignment = TextAlignment.CENTER
            alignment = Pos.CENTER
            style = "-fx-background-color: rgba(0, 0, 0, 0.82); -fx-border-color: #FFD700; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;"
        }
    }

    /**
     * Crea la etiqueta del contador de tiempo para las partidas de MRUV.
     */
    fun crearEtiquetaTemporizador(textoInicial: String = "01:00"): Label {
        return Label(textoInicial).apply {
            font = Font.font(fuenteFamilia, FontWeight.BOLD, 22.0)
            textFill = Color.web("#FFD700")
        }
    }

    /**
     * Crea una etiqueta descriptiva para formularios de simulación.
     */
    fun crearEtiquetaFormulario(etiqueta: String): Label {
        return Label(etiqueta).apply {
            font = Font.font(fuenteFamilia, FontWeight.BOLD, 11.0)
            textFill = Color.LIGHTGRAY
            prefWidth = 76.0
        }
    }

    /**
     * Crea un TextField compacto con estilo oscuro para ingresar parámetros numéricos.
     */
    fun crearCampoFormulario(valorInicial: String, prompt: String): TextField {
        return TextField(valorInicial).apply {
            promptText = prompt
            prefWidth = 70.0
            font = Font.font(fuenteFamilia, FontWeight.BOLD, 11.0)
            style = "-fx-background-color: #1A1A1A; -fx-text-fill: white; -fx-border-color: #555555; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 3 5;"
        }
    }

    /**
     * Posiciona un nodo dentro de un StackPane con alineación y margen uniforme.
     */
    fun posicionarEnStack(nodo: Node, alineacion: Pos, margen: Double) {
        fontRutaReferencia()
        StackPane.setAlignment(nodo, alineacion)
        if (margen > 0.0) StackPane.setMargin(nodo, Insets(margen))
    }

    private fun fontRutaReferencia(): String = fuenteFamilia
}
