package com.example.juegofisica

import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Pantalla del Modo Fácil del nivel MRUV que extiende [PantallaJuegoMRUVBase] e inyecta
 * la estrategia [CalculoTiempoFacil] cumpliendo con el Principio Abierto/Cerrado (OCP)
 * y el Principio de Sustitución de Liskov (LSP).
 */
class PantallaMRUV(
    stage: Stage,
    menuScene: Scene,
    validador: ValidadorTiempoMRUV = ValidadorTiempoMRUV()
) : PantallaJuegoMRUVBase(
    stage = stage,
    menuScene = menuScene,
    estrategiaCalculo = CalculoTiempoFacil(validador)
) {

    override fun esModoDificil(): Boolean = false

    /** Devuelve el tiempo teórico esperado (s) calculado por la estrategia activa. */
    fun obtenerTiempoEsperado(): Double = estrategiaCalculo.resultadoEsperado
}
