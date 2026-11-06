package com.example.juegofisica

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
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
import javafx.scene.text.TextAlignment

/**
 * Fábrica y proveedor de componentes visuales compartidos de la interfaz de usuario (DRY / SRP).
 *
 * Centraliza la creación y estilos de elementos comunes como botones interactivos con hover,
 * paneles de datos físicos y el ensamblaje de las pantallas de juego MRUV.
 */
class ComponentesUI {

    /**
     * Crea un ImageView responsivo vinculado al tamaño del contenedor raíz StackPane.
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
     * Crea un botón estilizado con efecto hover reutilizable para eliminar duplicación en las vistas.
     */
    fun crearBotonConHover(
        texto: String,
        estiloNormal: String,
        estiloHover: String,
        tamanoFuente: Double = 14.0,
        pesoFuente: FontWeight = FontWeight.BOLD,
        anchoPreferido: Double = 0.0,
        onAccion: () -> Unit
    ): Button {
        return Button(texto).apply {
            font = Font.font("System", pesoFuente, tamanoFuente)
            if (anchoPreferido > 0.0) {
                prefWidth = anchoPreferido
            }
            style = estiloNormal
            onMouseEntered = { style = estiloHover }
            onMouseExited = { style = estiloNormal }
            setOnAction { onAccion() }
        }
    }

    /**
     * Construye el botón estándar "Volver al Menú" con sus efectos de hover y acción asignada.
     */
    fun crearBotonVolver(onVolver: () -> Unit): Button {
        val normal = "-fx-background-color: #333333; -fx-text-fill: white; " +
                "-fx-border-color: #666666; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 16;"
        val hover = "-fx-background-color: #444444; -fx-text-fill: white; " +
                "-fx-border-color: #888888; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 16;"
        return crearBotonConHover("Volver al Menú", normal, hover, 14.0, FontWeight.NORMAL, 0.0, onVolver)
    }

    /**
     * Construye el botón estándar "REGLAS" con acento dorado y efecto hover.
     */
    fun crearBotonReglas(onMostrarReglas: () -> Unit): Button {
        val normal = "-fx-background-color: #1a1a1a; -fx-text-fill: #FFD700; " +
                "-fx-border-color: #FFD700; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 16;"
        val hover = "-fx-background-color: #2a2a00; -fx-text-fill: #FFD700; " +
                "-fx-border-color: #FFAA00; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 16;"
        return crearBotonConHover("REGLAS", normal, hover, 14.0, FontWeight.BOLD, 0.0, onMostrarReglas)
    }

    /**
     * Crea una etiqueta formateada en tipografía dorada para mostrar variables físicas.
     */
    fun crearEtiquetaDato(texto: String): Label {
        return Label(texto).apply {
            font = Font.font("System", FontWeight.BOLD, 13.0)
            textFill = Color.web("#FFD700")
        }
    }

    /**
     * Crea un panel contenedor translúcido alineado a la derecha con borde dorado para agrupar datos.
     */
    fun crearPanelDatos(vararg etiquetas: Label): VBox {
        return VBox(4.0).apply {
            alignment = Pos.TOP_RIGHT
            padding = Insets(8.0, 10.0, 8.0, 10.0)
            maxWidth = Region.USE_PREF_SIZE
            style = "-fx-background-color: rgba(0, 0, 0, 0.75); -fx-border-color: #FFD700; " +
                    "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;"
            children.addAll(*etiquetas)
        }
    }

    /**
     * Crea el contenedor destacado superior con el mensaje de atención para las pantallas de juego MRUV.
     */
    fun crearCartelAtencion(): Label {
        val mensaje = "ATENCION: Este juego consiste en hacer la cuenta matematica correspondiente " +
                "a la dificultad en menos de un minuto, la formula se encuentra en el apartado reglas " +
                "dentro del nivel, los datos para resolver esa cuanta estan debajo del apartado reglas"
        return Label(mensaje).apply {
            isWrapText = true
            maxWidth = 215.0
            maxHeight = Region.USE_PREF_SIZE
            padding = Insets(6.0, 10.0, 6.0, 10.0)
            font = Font.font("System", FontWeight.BOLD, 10.0)
            textFill = Color.web("#FFD700")
            textAlignment = TextAlignment.CENTER
            alignment = Pos.CENTER
            style = "-fx-background-color: rgba(0, 0, 0, 0.82); -fx-border-color: #FFD700; " +
                    "-fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;"
        }
    }

    /**
     * Crea la etiqueta del contador de tiempo para las partidas de MRUV.
     */
    fun crearEtiquetaTemporizador(textoInicial: String = "01:00"): Label {
        return Label(textoInicial).apply {
            font = Font.font("System", FontWeight.BOLD, 22.0)
            textFill = Color.web("#FFD700")
        }
    }

    /**
     * Ensambla y posiciona las capas comunes de una partida MRUV dentro del StackPane raíz (DRY).
     */
    fun ensamblarCapasJuegoMRUV(
        root: StackPane,
        lienzo: Node,
        zonaIzquierda: VBox,
        zonaDerecha: VBox,
        lblContador: Label,
        cuadroReglas: Node,
        overlayDerrota: Node
    ) {
        val cartelAtencion = crearCartelAtencion()
        root.children.addAll(
            lienzo,
            cartelAtencion,
            zonaIzquierda,
            zonaDerecha,
            lblContador,
            cuadroReglas,
            overlayDerrota
        )

        StackPane.setAlignment(cartelAtencion, Pos.TOP_CENTER)
        StackPane.setMargin(cartelAtencion, Insets(12.0, 0.0, 0.0, 0.0))
        StackPane.setAlignment(zonaIzquierda, Pos.TOP_LEFT)
        StackPane.setMargin(zonaIzquierda, Insets(15.0))
        StackPane.setAlignment(zonaDerecha, Pos.TOP_RIGHT)
        StackPane.setMargin(zonaDerecha, Insets(15.0))
        StackPane.setAlignment(lblContador, Pos.BOTTOM_LEFT)
        StackPane.setMargin(lblContador, Insets(15.0))
        StackPane.setAlignment(cuadroReglas, Pos.CENTER)
        StackPane.setAlignment(overlayDerrota, Pos.CENTER)
    }
}
