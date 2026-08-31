package com.example.juegofisica

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage

/**
 * Clase principal de la aplicación JavaFX.
 * Extiende Application y configura la ventana inicial del juego educativo de física.
 */
class InterfazDeInicio : Application() {

    override fun start(primaryStage: Stage) {
        // Contenedor principal: VBox centrado con espaciado y padding
        val root = VBox(20.0).apply {
            alignment = Pos.CENTER
            padding = Insets(20.0)
            styleClass.add("root-container")
        }

        // Sección del encabezado (título + subtítulo)
        val headerSection = HeaderSection()
        root.children.add(headerSection.construir())

        // Sección de botones del menú
        val menuButtonsSection = MenuButtonsSection()
        root.children.add(menuButtonsSection.construir())

        // Crear escena con dimensiones 400x300
        val scene = Scene(root, 400.0, 300.0)

        // Cargar estilos CSS opcionales (si existe el archivo)
        // scene.stylesheets.add(getClass().getResource("/styles.css").toExternalForm())

        // Configurar y mostrar la ventana principal
        primaryStage.title = "Juego Educativo de Física"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

/**
 * Componente responsable de construir la sección de texto del encabezado.
 * Contiene el título principal "Bienvenido" y el subtítulo "elige 1".
 */
class HeaderSection {

    /**
     * Construye y retorna un VBox con los textos del encabezado.
     */
    fun construir(): VBox {
        return VBox(8.0).apply {
            alignment = Pos.CENTER

            // Texto principal: "Bienvenido" - estilo destacado, tamaño grande
            val titulo = Text("Bienvenido").apply {
                font = Font.font("System", FontWeight.BOLD, 36.0)
                styleClass.add("titulo-principal")
            }

            // Subtítulo: "elige 1" - ubicado justo debajo
            val subtitulo = Text("elige 1").apply {
                font = Font.font("System", FontWeight.NORMAL, 18.0)
                styleClass.add("subtitulo")
            }

            children.addAll(titulo, subtitulo)
        }
    }
}

/**
 * Componente responsable de construir y organizar el grupo de botones del menú.
 * Crea los botones para las opciones físicas usando la clase PhysicsButton.
 */
class MenuButtonsSection {

    /**
     * Construye y retorna un VBox con los botones de selección.
     */
    fun construir(): VBox {
        return VBox(15.0).apply {
            alignment = Pos.CENTER
            // Ancho fijo para que ambos botones tengan el mismo tamaño
            maxWidth = 250.0

            // Botón 1: "Tiro Oblicuo" - usa PhysicsButton personalizado
            val btnTiroOblicuo = PhysicsButton("Tiro Oblicuo")

            // Botón 2: "MRUV" - usa PhysicsButton personalizado
            val btnMRUV = PhysicsButton("MRUV")

            children.addAll(btnTiroOblicuo, btnMRUV)
        }
    }
}

/**
 * Botón personalizado para las opciones del menú de física.
 * Extiende Button de JavaFX y aplica un estilo visual consistente.
 * Sin eventos de clic por ahora (se pueden agregar luego).
 */
class PhysicsButton(texto: String) : Button(texto) {

    init {
        // Configuración visual del botón
        prefWidth = 250.0
        prefHeight = 50.0
        font = Font.font("System", FontWeight.MEDIUM, 16.0)
        styleClass.add("physics-button")

        // Efecto visual: cursor de mano al pasar por encima
        onMouseEntered = { cursor = javafx.scene.Cursor.HAND }
        onMouseExited = { cursor = javafx.scene.Cursor.DEFAULT }

        // Por ahora sin acción al hacer clic (se implementará después)
        // setOnAction { println("$texto seleccionado") }
    }
}