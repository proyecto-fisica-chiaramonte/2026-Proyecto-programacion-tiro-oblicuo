package com.example.juegofisica

import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.stage.Stage

/**
 * Controlador de ciclo de vida, puntaje, temporizador y navegación para las partidas de MRUV (SRP).
 *
 * Mantiene todos sus atributos estrictamente privados (`private`) y expone el estado únicamente
 * a través de métodos con comportamiento de dominio.
 */
class GestorPartidaMRUV(
    private val stage: Stage,
    private val menuScene: Scene,
    private val esModoDificil: Boolean
) {

    /** Estado numérico de la partida: [tiempoInicial, tiempoRestante, puntajeActual, finalizada(0/1)]. */
    private val estadoPartida: IntArray = intArrayOf(60, 60, 0, 0)

    /** Temporizador activo de la partida. */
    private var temporizador: TemporizadorPartida? = null

    /** Devuelve la duración inicial de la partida en segundos. */
    fun obtenerTiempoInicial(): Int = estadoPartida[0]

    /** Devuelve los segundos restantes de la cuenta regresiva actual. */
    fun obtenerTiempoRestante(): Int = estadoPartida[1]

    /** Devuelve el puntaje actual obtenido por el jugador. */
    fun obtenerPuntajeActual(): Int = estadoPartida[2]

    /** Indica si la partida actual ya se encuentra finalizada. */
    fun estaFinalizada(): Boolean = estadoPartida[3] == 1

    /**
     * Registra un acierto del jugador, incrementa el puntaje y marca la partida como completada.
     */
    fun registrarAcierto(animacionEscenario: AnimacionEscenario) {
        if (estaFinalizada()) return
        estadoPartida[3] = 1
        estadoPartida[2] += if (esModoDificil) 200 else 100
        detenerTodo(animacionEscenario)
    }

    /**
     * Inicia el temporizador de 60 segundos vinculándolo al progreso del tren y a la secuencia de impacto.
     */
    fun iniciarTemporizador(lblContador: Label, animacion: AnimacionEscenario, alAgotarTiempo: () -> Unit) {
        estadoPartida[3] = 0
        estadoPartida[1] = estadoPartida[0]
        val gestor = TemporizadorPartida(
            onTick = { segs, texto ->
                estadoPartida[1] = segs
                lblContador.text = texto
                animacion.actualizarProgreso((estadoPartida[0] - segs).toDouble() / estadoPartida[0].toDouble())
            },
            onFin = { procesarTiempoAgotado(lblContador, alAgotarTiempo) }
        )
        temporizador = gestor
        lblContador.text = gestor.formatearTiempo(estadoPartida[0])
        gestor.iniciar(estadoPartida[0])
    }

    /**
     * Detiene el temporizador y la animación activa.
     */
    fun detenerTodo(animacionEscenario: AnimacionEscenario) {
        estadoPartida[0] = 60
        temporizador?.detener()
        animacionEscenario.detener()
    }

    /**
     * Detiene recursos y regresa al menú principal.
     */
    fun volverAlMenu(animacionEscenario: AnimacionEscenario) {
        estadoPartida[3] = 1
        detenerTodo(animacionEscenario)
        stage.scene = menuScene
        stage.title = "Juego Educativo de Física"
    }

    /**
     * Detiene el temporizador y la animación y muestra la pantalla de victoria con el tiempo empleado.
     */
    fun mostrarVictoria(animacionEscenario: AnimacionEscenario) {
        registrarAcierto(animacionEscenario)
        val tiempoEmpleado = (estadoPartida[0] - estadoPartida[1]).coerceIn(0, estadoPartida[0])
        val pantallaVictoria = PantallaVictoriaMRUV(stage, menuScene, tiempoEmpleado).configurarModo(esModoDificil)
        stage.scene = pantallaVictoria.crearEscena()
        stage.title = "MRUV - ¡Victoria!"
    }

    private fun procesarTiempoAgotado(lblContador: Label, alAgotarTiempo: () -> Unit) {
        estadoPartida[3] = 1
        estadoPartida[1] = 0
        lblContador.text = "00:00"
        lblContador.textFill = Color.web("#FF4444")
        alAgotarTiempo()
    }
}
