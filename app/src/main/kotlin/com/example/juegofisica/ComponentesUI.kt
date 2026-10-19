package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

/**
 * Fábrica y proveedor de componentes visuales compartidos de la interfaz de usuario.
 *
 * Centraliza la creación y estilos de elementos comunes como fondos responsivos,
 * botones con hover estándar y paneles de datos para evitar duplicación de código (DRY).
 */
class ComponentesUI {

    /**
     * Crea un ImageView responsivo vinculado al tamaño del contenedor raíz StackPane.
     *
     * @param rutaRecurso ruta absoluta al archivo de imagen en resources.
     * @param root contenedor StackPane del cual se vincularán las dimensiones.
     * @return ImageView configurado y vinculado.
     */
    fun crearFondoResponsive(rutaRecurso: String, root: StackPane): ImageView {
        val imagenFondo = Image(javaClass.getResource(rutaRecurso).toExternalForm())
        return ImageView(imagenFondo).apply {
            isPreserveRatio = false
            fitWidthProperty().bind(root.widthProperty())
            fitHeightProperty().bind(root.heightProperty())
        }
    }

    /**
     * Construye el botón estándar "Volver al Menú" con sus efectos de hover y acción asignada.
     *
     * @param onVolver acción a ejecutar al presionar el botón.
     * @return instancia de Button con estilo estándar.
     */
    fun crearBotonVolver(onVolver: () -> Unit): Button {
        return Button("Volver al Menú").apply {
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
            setOnAction { onVolver() }
        }
    }

    /**
     * Construye el botón estándar "REGLAS" con acento dorado y efecto hover.
     *
     * @param onMostrarReglas acción a ejecutar para mostrar el cuadro de reglas.
     * @return instancia de Button configurada.
     */
    fun crearBotonReglas(onMostrarReglas: () -> Unit): Button {
        return Button("REGLAS").apply {
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
            setOnAction { onMostrarReglas() }
        }
    }

    /**
     * Crea una etiqueta formateada en tipografía dorada para mostrar variables físicas.
     *
     * @param texto contenido textual de la variable y su valor.
     * @return Label con el estilo consistente.
     */
    fun crearEtiquetaDato(texto: String): Label {
        return Label(texto).apply {
            font = Font.font("System", FontWeight.BOLD, 13.0)
            textFill = Color.web("#FFD700")
        }
    }

    /**
     * Crea un panel contenedor translúcido alineado a la derecha con borde dorado para agrupar datos.
     *
     * @param etiquetas etiquetas a añadir dentro del panel.
     * @return VBox contenedor de datos estilizado.
     */
    fun crearPanelDatos(vararg etiquetas: Label): VBox {
        return VBox(4.0).apply {
            alignment = Pos.TOP_RIGHT
            padding = Insets(8.0, 10.0, 8.0, 10.0)
            maxWidth = Region.USE_PREF_SIZE
            style = "-fx-background-color: rgba(0, 0, 0, 0.75); " +
                    "-fx-border-color: #FFD700; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 8; " +
                    "-fx-background-radius: 8;"
            children.addAll(*etiquetas)
        }
    }
}
