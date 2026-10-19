package com.example.juegofisica

import kotlin.math.sqrt

/**
 * Motor de lógica y cálculo aritmético para la calculadora.
 *
 * Implementa [ICalculadora] aplicando el Principio de Responsabilidad Única (SRP),
 * gestionando el estado numérico, operadores pendientes, cálculos binarios,
 * raíces cuadradas y formateo de resultados sin dependencias de la interfaz de usuario.
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
        if (iniciarNumeroNuevo || textoDisplay == "Error") {
            textoDisplay = digito
            iniciarNumeroNuevo = false
        } else if (textoDisplay == "0") {
            textoDisplay = digito
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
        if (textoDisplay == "Error") {
            limpiar()
            return textoDisplay
        }
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
        if (valor == null || valor < 0.0) {
            return mostrarError()
        }
        textoDisplay = formatearNumero(sqrt(valor))
        iniciarNumeroNuevo = true
        return textoDisplay
    }

    override fun calcularResultado(): String {
        val operador = operadorPendiente ?: return textoDisplay
        val izquierdo = operandoAnterior ?: return textoDisplay
        val derecho = textoDisplay.toDoubleOrNull() ?: return textoDisplay

        val resultado = when (operador) {
            "+" -> izquierdo + derecho
            "-" -> izquierdo - derecho
            "*" -> izquierdo * derecho
            "/" -> {
                if (derecho == 0.0) {
                    return mostrarError()
                }
                izquierdo / derecho
            }
            else -> return textoDisplay
        }

        textoDisplay = formatearNumero(resultado)
        operandoAnterior = null
        operadorPendiente = null
        iniciarNumeroNuevo = true
        return textoDisplay
    }

    override fun limpiar(): String {
        textoDisplay = "0"
        operandoAnterior = null
        operadorPendiente = null
        iniciarNumeroNuevo = true
        return textoDisplay
    }

    /**
     * Establece el estado de error y limpia los operandos pendientes.
     */
    private fun mostrarError(): String {
        textoDisplay = "Error"
        operandoAnterior = null
        operadorPendiente = null
        iniciarNumeroNuevo = true
        return textoDisplay
    }

    /**
     * Formatea un número double evitando decimales innecesarios cuando es entero.
     */
    private fun formatearNumero(valor: Double): String {
        return if (valor % 1.0 == 0.0 && valor in -1_000_000_000.0..1_000_000_000.0) {
            valor.toLong().toString()
        } else {
            String.format("%.6f", valor).trimEnd('0').trimEnd('.')
        }
    }
}
