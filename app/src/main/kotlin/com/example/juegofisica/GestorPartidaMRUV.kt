package com.example.juegofisica

import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.stage.Stage

/**
 * Controlador de estado y ciclo de vida de una partida de MRUV (Principio de Responsabilidad Única - SRP).
 *
 * Encapsula la generación de variables físicas aleatorias, la sincronización del temporizador
 * de 60 segundos con [AnimacionEscenario] y las transiciones hacia el menú o la pantalla de victoria.
 */
class GestorPartidaMRUV(
    private val stage: Stage,
    private val menuScene: Scene,
    private val esModoDificil: Boolean
) {

    /** Duración máxima fija de la partida (60 segundos = 01:00). */
    val tiempoInicialSegundos: Int = 60

    /** Segundos restantes de la partida en curso. */
    var tiempoRestanteSegundos: Int = 60
        private set

    /** Aceleración generada para el problema (m/s²). */
    var aceleracion: Double = 0.0
        private set

    /** Distancia generada para el problema (m). */
    var distancia: Double = 0.0
        private set

    /** Velocidad constante generada para el modo difícil (m/s). */
    var velocidad: Double = 0.0
        private set

    /** Velocidad inicial generada para el problema (m/s). */
    var velocidadInicial: Double = 0.0
        private set

    /** Posición inicial generada para el problema (m). */
    var posicionInicial: Double = 0.0
        private set

    /** Valor teórico esperado de la respuesta (t en modo fácil, x(t) en modo difícil). */
    var resultadoEsperado: Double = 0.0
        private set

    /** Temporizador activo de la partida. */
    private var temporizador: TemporizadorPartida? = null

    /**
     * Genera variables físicas aleatorias para el Modo Fácil donde t = √(2 · d / a) es entero.
     */
    fun generarDatosModoFacil(validador: ValidadorTiempoMRUV) {
        val tiempoExacto = listOf(2, 3, 4, 5, 6).random()
        val opcionesA = listOf(1, 2, 4).filter { a -> (a * tiempoExacto * tiempoExacto) % 2 == 0 }
        val a = if (opcionesA.isNotEmpty()) opcionesA.random() else 2
        aceleracion = a.toDouble()
        distancia = (a * tiempoExacto * tiempoExacto / 2).toDouble()
        velocidadInicial = (0..15).random().toDouble()
        posicionInicial = (0..20).random().toDouble()
        resultadoEsperado = validador.calcularTiempo(distancia, aceleracion)
    }

    /**
     * Genera variables físicas aleatorias para el Modo Difícil donde t = d / v es entero.
     */
    fun generarDatosModoDificil(validador: IValidadorRespuesta) {
        val v = (5..30).random()
        val tiempoExacto = listOf(2, 3, 4, 5, 6, 8, 10).random()
        velocidad = v.toDouble()
        distancia = (v * tiempoExacto).toDouble()
        aceleracion = (1..10).random().toDouble()
        velocidadInicial = (0..20).random().toDouble()
        posicionInicial = (0..50).random().toDouble()
        resultadoEsperado = validador.calcularPosicionFinal(
            posicionInicial = posicionInicial,
            velocidadInicial = velocidadInicial,
            aceleracion = aceleracion,
            distancia = distancia,
            velocidad = velocidad
        )
    }

    /**
     * Inicia el temporizador de 60 segundos vinculándolo al progreso del tren y a la secuencia de impacto.
     */
    fun iniciarTemporizador(
        lblContador: Label,
        animacionEscenario: AnimacionEscenario,
        casillaRespuesta: CasillaRespuestaWidget,
        pantallaDerrota: PantallaDerrotaMRUV
    ) {
        val gestor = TemporizadorPartida(
            onTick = { segundos, tiempoFormateado ->
                tiempoRestanteSegundos = segundos
                lblContador.text = tiempoFormateado
                val progreso = (tiempoInicialSegundos - segundos).toDouble() / tiempoInicialSegundos.toDouble()
                animacionEscenario.actualizarProgreso(progreso)
            },
            onFin = {
                tiempoRestanteSegundos = 0
                lblContador.text = "00:00"
                lblContador.textFill = Color.web("#FF4444")
                casillaRespuesta.deshabilitar()
                animacionEscenario.ejecutarAnimacionImpacto { pantallaDerrota.mostrar() }
            }
        )
        temporizador = gestor
        lblContador.text = gestor.formatearTiempo(tiempoInicialSegundos)
        gestor.iniciar(tiempoInicialSegundos)
    }

    /**
     * Detiene el temporizador y la animación activa.
     */
    fun detenerTodo(animacionEscenario: AnimacionEscenario) {
        temporizador?.detener()
        animacionEscenario.detener()
    }

    /**
     * Detiene recursos y regresa al menú principal.
     */
    fun volverAlMenu(animacionEscenario: AnimacionEscenario) {
        detenerTodo(animacionEscenario)
        stage.scene = menuScene
        stage.title = "Juego Educativo de Física"
    }

    /**
     * Detiene el temporizador y la animación y muestra la pantalla de victoria con el tiempo empleado.
     */
    fun mostrarVictoria(animacionEscenario: AnimacionEscenario) {
        val tiempoEmpleado = (tiempoInicialSegundos - tiempoRestanteSegundos).coerceIn(0, tiempoInicialSegundos)
        detenerTodo(animacionEscenario)
        val pantallaVictoria = PantallaVictoriaMRUV(stage, menuScene, tiempoEmpleado, esModoDificil)
        stage.scene = pantallaVictoria.crearEscena()
        stage.title = "MRUV - ¡Victoria!"
    }
}
