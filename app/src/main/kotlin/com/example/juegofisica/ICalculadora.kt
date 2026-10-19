package com.example.juegofisica

/**
 * Contrato de abstracción para el motor de operaciones de la calculadora.
 *
 * Aplica el Principio de Segregación de Interfaces (ISP) e Inversión de Dependencias (DIP),
 * permitiendo desacoplar la lógica matemática de la representación en la interfaz JavaFX.
 */
interface ICalculadora {

    /** Texto actual que debe mostrarse en la pantalla o display. */
    val display: String

    /** Ingresa un dígito numérico (0 al 9) en la operación. */
    fun ingresarDigito(digito: String): String

    /** Ingresa el punto decimal en el número actual. */
    fun ingresarPunto(): String

    /** Establece un operador aritmético binario (+, -, *, /). */
    fun establecerOperador(operador: String): String

    /** Aplica la raíz cuadrada al valor actualmente mostrado. */
    fun aplicarRaiz(): String

    /** Evalúa la operación pendiente y devuelve el resultado. */
    fun calcularResultado(): String

    /** Reinicia el estado de la calculadora a sus valores iniciales. */
    fun limpiar(): String
}
