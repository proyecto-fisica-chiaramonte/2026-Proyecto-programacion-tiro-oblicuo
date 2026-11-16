package com.example.juegofisica

import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Pantalla del Modo Difícil del nivel MRUV que extiende [PantallaJuegoMRUVBase] e inyecta
 * la estrategia [CalculoDistanciaDificil] cumpliendo con el Principio Abierto/Cerrado (OCP),
 * Sustitución de Liskov (LSP) e Inversión de Dependencias (DIP).
 */
class PantallaModoDificil(
    stage: Stage,
    menuScene: Scene,
    validador: IValidadorRespuesta = ValidadorRespuestaMRUV()
) : PantallaJuegoMRUVBase(
    stage = stage,
    menuScene = menuScene,
    estrategiaCalculo = CalculoDistanciaDificil(validador)
) {

    override fun esModoDificil(): Boolean = true

    /** Devuelve el resultado teórico esperado de x(t) calculado por la estrategia activa. */
    fun obtenerResultadoEsperado(): Double = estrategiaCalculo.resultadoEsperado
}
