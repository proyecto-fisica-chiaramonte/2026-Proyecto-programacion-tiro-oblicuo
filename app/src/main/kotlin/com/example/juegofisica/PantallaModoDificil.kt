package com.example.juegofisica

import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

/**
 * Pantalla correspondiente al modo difícil del nivel MRUV (SRP / DRY).
 *
 * Delega la gestión de datos, temporizador y navegación en [GestorPartidaMRUV], el renderizado
 * continuo en [AnimacionEscenario] y la creación de controles en [ComponentesUI].
 *
 * @property stage el Stage principal de la aplicación.
 * @property menuScene la Scene del menú principal a la que se regresa.
 * @property validador validador de cálculo físico desacoplado mediante interfaz [IValidadorRespuesta] (DIP).
 */
class PantallaModoDificil(
    private val stage: Stage,
    private val menuScene: Scene,
    private val validador: IValidadorRespuesta = ValidadorRespuestaMRUV()
) : PantallaJuego {

    /** Proveedor reutilizable de componentes visuales (DRY). */
    private val componentesUI: ComponentesUI = ComponentesUI()

    /** Gestor del ciclo de animación continua del escenario nocturno para el modo difícil (SRP). */
    private val animacionEscenario: AnimacionEscenario = AnimacionEscenario(esModoDificil = true)

    /** Controlador del estado de la partida, variables físicas y temporizador (SRP). */
    private val gestorPartida: GestorPartidaMRUV = GestorPartidaMRUV(stage, menuScene, esModoDificil = true)

    /** Duración fija de la partida en el modo difícil (60 segundos). */
    val tiempoInicialSegundos: Int
        get() = gestorPartida.tiempoInicialSegundos

    /** Segundos restantes de la partida en curso. */
    val tiempoRestanteSegundos: Int
        get() = gestorPartida.tiempoRestanteSegundos

    /** Resultado teórico esperado de x(t). */
    val resultadoEsperado: Double
        get() = gestorPartida.resultadoEsperado

    /**
     * Construye y devuelve la Scene del modo difícil de MRUV.
     */
    override fun crearEscena(): Scene {
        val root = StackPane()
        animacionEscenario.vincularTamano(root)
        animacionEscenario.iniciarPersecucion()

        gestorPartida.generarDatosModoDificil(validador)

        val pantallaDerrota = PantallaDerrotaMRUV(
            onReiniciar = { gestorPartida.volverAlMenu(animacionEscenario) },
            mensaje = "¡Perdiste!"
        )
        val casillaRespuesta = crearCasillaRespuesta(pantallaDerrota)
        val cuadroReglas = CuadroReglas("x(t) = x₀ + v₀ · t + ½ · a · t²", "t = d / v")

        val zonaIzquierda = construirZonaIzquierda(casillaRespuesta)
        val zonaDerecha = construirZonaDerecha(cuadroReglas)
        val lblContador = componentesUI.crearEtiquetaTemporizador()

        gestorPartida.iniciarTemporizador(lblContador, animacionEscenario, casillaRespuesta, pantallaDerrota)

        componentesUI.ensamblarCapasJuegoMRUV(
            root = root,
            lienzo = animacionEscenario.canvas,
            zonaIzquierda = zonaIzquierda,
            zonaDerecha = zonaDerecha,
            lblContador = lblContador,
            cuadroReglas = cuadroReglas.contenedor,
            overlayDerrota = pantallaDerrota.contenedor
        )

        return Scene(root, 400.0, 300.0)
    }

    /**
     * Transiciona a la pantalla de victoria delegando en [GestorPartidaMRUV].
     */
    fun mostrarVictoria() {
        gestorPartida.mostrarVictoria(animacionEscenario)
    }

    private fun crearCasillaRespuesta(pantallaDerrota: PantallaDerrotaMRUV): CasillaRespuestaWidget {
        lateinit var casilla: CasillaRespuestaWidget
        casilla = CasillaRespuestaWidget(
            onVerificar = { entrada ->
                val esCorrecto = validador.validar(entrada, gestorPartida.resultadoEsperado)
                if (esCorrecto) {
                    gestorPartida.detenerTodo(animacionEscenario)
                    casilla.deshabilitar()
                    animacionEscenario.ejecutarAnimacionDesvio { mostrarVictoria() }
                } else if (gestorPartida.tiempoRestanteSegundos <= 1) {
                    gestorPartida.detenerTodo(animacionEscenario)
                    casilla.deshabilitar()
                    animacionEscenario.ejecutarAnimacionImpacto { pantallaDerrota.mostrar() }
                }
                esCorrecto
            }
        )
        return casilla
    }

    private fun construirZonaIzquierda(casillaRespuesta: CasillaRespuestaWidget): VBox {
        val btnVolver = componentesUI.crearBotonVolver { gestorPartida.volverAlMenu(animacionEscenario) }
        val calculadora = CalculadoraWidget()
        return VBox(6.0, btnVolver, calculadora.contenedor, casillaRespuesta.contenedor).apply {
            alignment = Pos.TOP_LEFT
            maxWidth = Region.USE_PREF_SIZE
        }
    }

    private fun construirZonaDerecha(cuadroReglas: CuadroReglas): VBox {
        val btnReglas = componentesUI.crearBotonReglas { cuadroReglas.mostrar() }
        val panelDatos = componentesUI.crearPanelDatos(
            componentesUI.crearEtiquetaDato("a = ${gestorPartida.aceleracion.toInt()} m/s²"),
            componentesUI.crearEtiquetaDato("d = ${gestorPartida.distancia.toInt()} m"),
            componentesUI.crearEtiquetaDato("v = ${gestorPartida.velocidad.toInt()} m/s"),
            componentesUI.crearEtiquetaDato("v₀ = ${gestorPartida.velocidadInicial.toInt()} m/s"),
            componentesUI.crearEtiquetaDato("x₀ = ${gestorPartida.posicionInicial.toInt()} m")
        )
        return VBox(10.0, btnReglas, panelDatos).apply {
            alignment = Pos.TOP_RIGHT
            maxWidth = Region.USE_PREF_SIZE
        }
    }
}
