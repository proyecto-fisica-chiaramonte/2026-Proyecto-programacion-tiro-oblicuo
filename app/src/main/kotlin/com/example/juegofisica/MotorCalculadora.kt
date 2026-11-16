package com.example.juegofisica

import kotlin.math.sqrt

/**
 * Motor de lógica y cálculo aritmético para la calculadora (SRP / DIP).
 */
class MotorCalculadora : ICalculadora {

    /** Texto actual visible en el display. */
    private var textoDisplay: String = "0"

    /** Operando izquierdo guardado al seleccionar una operación. */
    private var operandoAnterior: Double? = null

    /** Operador pendiente de aplicar (+, -, *, /). */
    private var operadorPendiente: String? = null

    /** Indica si el próximo dígito debe iniciar un número nuevo. */
    private var iniciarNumeroNuevo: Boolean = true

    override val display: String
        get() = textoDisplay

    override fun ingresarDigito(digito: String): String {
        if (iniciarNumeroNuevo || textoDisplay == "Error" || textoDisplay == "0") {
            textoDisplay = digito
            iniciarNumeroNuevo = false
        } else {
            textoDisplay += digito
        }
        return textoDisplay
    }

    override fun ingresarPunto(): String {
        if (iniciarNumeroNuevo || textoDisplay == "Error") {
            textoDisplay = "0."
            iniciarNumeroNuevo = false
        } else if (!textoDisplay.contains(".")) {
            textoDisplay += "."
        }
        return textoDisplay
    }

    override fun establecerOperador(operador: String): String {
        if (textoDisplay == "Error") return limpiar()
        val valorActual = textoDisplay.toDoubleOrNull() ?: return textoDisplay
        if (operandoAnterior != null && operadorPendiente != null && !iniciarNumeroNuevo) {
            calcularResultado()
            if (textoDisplay == "Error") return textoDisplay
        }
        operandoAnterior = textoDisplay.toDoubleOrNull() ?: valorActual
        operadorPendiente = operador
        iniciarNumeroNuevo = true
        return textoDisplay
    }

    override fun aplicarRaiz(): String {
        val valor = textoDisplay.toDoubleOrNull()
        if (valor == null || valor < 0.0) return mostrarError()
        iniciarNumeroNuevo = true
        return actualizarConNumero(sqrt(valor))
    }

    override fun calcularResultado(): String {
        val op = operadorPendiente ?: return textoDisplay
        val izq = operandoAnterior ?: return textoDisplay
        val der = textoDisplay.toDoubleOrNull() ?: return textoDisplay
        if (op == "/" && der == 0.0) return mostrarError()
        val res = when (op) { "+" -> izq + der; "-" -> izq - der; "*" -> izq * der; "/" -> izq / der; else -> return textoDisplay }
        operandoAnterior = null
        operadorPendiente = null
        iniciarNumeroNuevo = true
        return actualizarConNumero(res)
    }

    override fun limpiar(): String {
        textoDisplay = "0"
        operandoAnterior = null
        operadorPendiente = null
        iniciarNumeroNuevo = true
        return textoDisplay
    }

    private fun mostrarError(): String {
        limpiar()
        textoDisplay = "Error"
        return textoDisplay
    }

    private fun actualizarConNumero(valor: Double): String {
        textoDisplay = if (valor % 1.0 == 0.0 && valor in -1_000_000_000.0..1_000_000_000.0) {
            valor.toLong().toString()
        } else {
            String.format("%.6f", valor).trimEnd('0').trimEnd('.')
        }
        return textoDisplay
    }
}
