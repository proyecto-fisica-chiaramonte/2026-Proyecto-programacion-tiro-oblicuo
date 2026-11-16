package com.example.juegofisica

/**
 * Abstracción para las estrategias de generación de datos y validación matemática en MRUV
 * (Principio Abierto/Cerrado - OCP y Principio de Inversión de Dependencias - DIP).
 *
 * Permite agregar nuevas dificultades o modos de cálculo (por ejemplo, un Modo Experto
 * para despejar aceleración o velocidad inicial) creando una nueva implementación sin
 * modificar las clases de pantalla.
 */
interface IEstrategiaCalculoMRUV {

    /** Fórmula principal que se muestra destacada en el cuadro de reglas del nivel. */
    val formulaPrincipal: String

    /** Fórmula complementaria que se muestra en el cuadro de reglas del nivel. */
    val formulaSecundaria: String

    /** Etiqueta corta de la incógnita a resolver en la casilla de respuesta (ej. "t =" o "X ="). */
    val etiquetaIncognita: String

    /** Resultado numérico teórico esperado para la partida actual. */
    val resultadoEsperado: Double

    /**
     * Genera un nuevo conjunto aleatorio de variables físicas para la partida y devuelve
     * las cadenas formateadas que deben mostrarse en el panel lateral de datos.
     *
     * @return lista de textos con los datos del problema (ej. "a = 2 m/s²", "d = 16 m").
     */
    fun generarNuevosDatos(): List<String>

    /**
     * Valida la respuesta ingresada por el jugador contra [resultadoEsperado].
     *
     * @param entradaUsuario texto ingresado en la casilla de respuesta.
     * @return true si el valor es correcto dentro de la tolerancia permitida.
     */
    fun validarRespuesta(entradaUsuario: String): Boolean
}
