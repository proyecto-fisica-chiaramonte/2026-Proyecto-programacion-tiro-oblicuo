package com.example.juegofisica

import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

/**
 * Clase base abstracta para las pantallas de juego del módulo MRUV (Patrón Template Method - OCP / DRY).
 *
 * Encapsula toda la estructura común (encabezado de atención, temporizador de 60s, escenario
 * animado en bucle, calculadora, cuadro de reglas, validación y transiciones de victoria/derrota)
 * delegando las variaciones específicas de cada dificultad en [IEstrategiaCalculoMRUV].
 */
abstract class PantallaJuegoMRUVBase(
    protected val stage: Stage,
    protected val menuScene: Scene,
    protected val estrategiaCalculo: IEstrategiaCalculoMRUV
) : PantallaJuego {

    /** Proveedor compartido de componentes visuales (DRY). */
    protected val componentesUI: ComponentesUI = ComponentesUI()

    /** Gestor del ciclo de animación continua del escenario, jugador y locomotora (SRP). */
    protected val animacionEscenario: AnimacionEscenario = AnimacionEscenario(esModoDificil())

    /** Controlador del temporizador de 60 segundos y transiciones de partida (SRP). */
    protected val gestorPartida: GestorPartidaMRUV = GestorPartidaMRUV(stage, menuScene, esModoDificil())

    /** Indica si la subclase corresponde al modo difícil (true) o fácil (false). */
    protected abstract fun esModoDificil(): Boolean

    /** Devuelve la duración inicial de la partida en segundos (60s). */
    fun obtenerTiempoInicialSegundos(): Int = gestorPartida.obtenerTiempoInicial()

    /** Devuelve los segundos restantes de la partida actual. */
    fun obtenerTiempoRestanteSegundos(): Int = gestorPartida.obtenerTiempoRestante()

    /**
     * Método plantilla (Template Method) que construye la Scene completa del nivel MRUV.
     */
    override fun crearEscena(): Scene {
        val root = StackPane()
        animacionEscenario.vincularTamano(root)
        animacionEscenario.iniciarPersecucion()

        val textosDatos = estrategiaCalculo.generarNuevosDatos()
        val pantallaDerrota = PantallaDerrotaMRUV({ gestorPartida.volverAlMenu(animacionEscenario) }, "¡Perdiste!")
        val casilla = crearCasillaRespuesta(pantallaDerrota)
        val reglas = CuadroReglas(estrategiaCalculo.formulaPrincipal, estrategiaCalculo.formulaSecundaria)
        val lblContador = componentesUI.crearEtiquetaTemporizador()

        iniciarRelojPartida(lblContador, casilla, pantallaDerrota)
        ensamblarVistaRaiz(root, listOf(construirZonaIzquierda(casilla), construirZonaDerecha(reglas, textosDatos)), listOf(lblContador, reglas.obtenerContenedor(), pantallaDerrota.obtenerContenedor()))
        return Scene(root, 400.0, 300.0)
    }

    /**
     * Detiene el temporizador y la animación activa y muestra la pantalla de victoria.
     */
    fun mostrarVictoria() {
        gestorPartida.mostrarVictoria(animacionEscenario)
    }

    private fun iniciarRelojPartida(lbl: javafx.scene.control.Label, casilla: CasillaRespuestaWidget, derrota: PantallaDerrotaMRUV) {
        gestorPartida.iniciarTemporizador(lbl, animacionEscenario) {
            casilla.deshabilitar()
            animacionEscenario.ejecutarAnimacionImpacto { derrota.mostrar() }
        }
    }

    private fun ensamblarVistaRaiz(root: StackPane, panelesLaterales: List<VBox>, superposiciones: List<javafx.scene.Node>) {
        val cartel = componentesUI.crearCartelAtencion()
        root.children.addAll(listOf(animacionEscenario.obtenerCanvas(), cartel) + panelesLaterales + superposiciones)
        componentesUI.posicionarEnStack(cartel, Pos.TOP_CENTER, 12.0)
        componentesUI.posicionarEnStack(panelesLaterales[0], Pos.TOP_LEFT, 15.0)
        componentesUI.posicionarEnStack(panelesLaterales[1], Pos.TOP_RIGHT, 15.0)
        componentesUI.posicionarEnStack(superposiciones[0], Pos.BOTTOM_LEFT, 15.0)
        componentesUI.posicionarEnStack(superposiciones[1], Pos.CENTER, 0.0)
        componentesUI.posicionarEnStack(superposiciones[2], Pos.CENTER, 0.0)
    }

    private fun crearCasillaRespuesta(pantallaDerrota: PantallaDerrotaMRUV): CasillaRespuestaWidget {
        lateinit var casilla: CasillaRespuestaWidget
        casilla = CasillaRespuestaWidget(
            onVerificar = { entrada -> procesarIntento(entrada, casilla, pantallaDerrota) },
            etiquetaTexto = estrategiaCalculo.etiquetaIncognita
        )
        return casilla
    }

    private fun procesarIntento(entrada: String, casilla: CasillaRespuestaWidget, derrota: PantallaDerrotaMRUV): Boolean {
        val ok = estrategiaCalculo.validarRespuesta(entrada)
        if (ok) {
            gestorPartida.registrarAcierto(animacionEscenario)
            casilla.deshabilitar()
            animacionEscenario.ejecutarAnimacionDesvio { mostrarVictoria() }
        } else if (esModoDificil() && gestorPartida.obtenerTiempoRestante() <= 1) {
            gestorPartida.detenerTodo(animacionEscenario)
            casilla.deshabilitar()
            animacionEscenario.ejecutarAnimacionImpacto { derrota.mostrar() }
        }
        return ok
    }

    private fun construirZonaIzquierda(casilla: CasillaRespuestaWidget): VBox {
        val btnVolver = componentesUI.crearBotonVolver { gestorPartida.volverAlMenu(animacionEscenario) }
        val calc = CalculadoraWidget()
        return VBox(6.0, btnVolver, calc.obtenerContenedor(), casilla.obtenerContenedor()).apply {
            alignment = Pos.TOP_LEFT
            maxWidth = Region.USE_PREF_SIZE
        }
    }

    private fun construirZonaDerecha(reglas: CuadroReglas, textosDatos: List<String>): VBox {
        val btnReglas = componentesUI.crearBotonReglas { reglas.mostrar() }
        val panelDatos = componentesUI.crearPanelDatos(textosDatos)
        return VBox(10.0, btnReglas, panelDatos).apply {
            alignment = Pos.TOP_RIGHT
            maxWidth = Region.USE_PREF_SIZE
        }
    }
}
