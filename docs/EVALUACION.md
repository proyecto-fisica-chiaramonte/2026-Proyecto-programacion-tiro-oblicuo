<!-- evaluacion:inicio -->
# Evaluación — proyecto-fisica-chiaramonte/2026-Proyecto-programacion-tiro-oblicuo

> Documento **generado automáticamente** por la cátedra de Programación II (RepoSense + análisis de calidad POO/SOLID). **No editar a mano**: se regenera en cada evaluación.
> Rama evaluada: `main`. Análisis **heurístico** (regex + métricas): es una criba para orientar la revisión, **no una nota**.
> Generado: 2026-11-20.

## 1. Veredicto

| Aspecto | Resultado |
|---|---|
| Índice de calidad POO/SOLID | **92/100** |
| Código (LOC) | 2,616 |
| Archivos de código | 41 |
| Clases (bien usadas) | 39 (31 OK) |
| Código dentro de clases (%POO) | 98% |
| Funciones fuera de clases | 0 |
| Duplicación lógica | 1.2% |
| Señales S/O/L/I/D | 3/0/0/0/1 |

> Este informe **describe** lo que el análisis midió con los criterios ya establecidos de la materia. No es una nota.

### Semáforo de aprobación

Criterios de `CRITERIOS-CALIDAD.md`. **Aprobar** se juega con el perfil `estandar`; **destacar**, con el `exigente` (meta de excelencia).

| Semáforo | Resultado |
|---|---|
| Aprobar (`estandar`) | **no cumple** — 3/4 |
| Destacar (`exigente`) | **no cumple** — 4/5 |

**Criterios para aprobar**

| Criterio | Requisito | Tu resultado | Cumple |
|---|---|---|---|
| Código dentro de clases (%POO) | ≥ 85% | 98% | sí |
| Duplicación lógica | ≤ 10% | 1.2% | sí |
| Funciones fuera de clases | ≤ 1 | 0 | sí |
| Señales POO/SOLID críticas | ninguna | Baja cohesión (LCOM), Acoplamiento alto (CBO) | no |

**Criterios para destacar**

| Criterio | Requisito | Tu resultado | Cumple |
|---|---|---|---|
| Código dentro de clases (%POO) | ≥ 95% | 98% | sí |
| Duplicación lógica | ≤ 5% | 1.2% | sí |
| Funciones fuera de clases | ≤ 1 | 0 | sí |
| Señales POO/SOLID críticas | ninguna | Baja cohesión (LCOM), Acoplamiento alto (CBO) | no |
| Índice de calidad POO/SOLID | ≥ 60 | 92/100 | sí |

### Cómo se calcula el %POO (y por qué no sube solo)

`%POO = líneas de lógica dentro de clases ÷ líneas de lógica totales`.

Se cuentan las líneas de **lógica y campos**; no cuentan `package`/`import`/`using`/anotaciones ni comentarios, Javadoc o docstrings. Es una **proporción**: si agregás código nuevo dentro de una clase, el numerador y el denominador crecen juntos y el porcentaje casi no cambia.

Sube cuando el código que hoy está **fuera** de clases pasa a ser método de una clase (o desaparece): funciones sueltas, `main`, constantes de módulo, scripts y lógica de archivos sueltos. También importa que ese trabajo esté **integrado en `main`**: la evaluación siempre mide `main`, así que lo que sigue en otra rama no se refleja todavía.

## 2. Autoría (RepoSense)

| Autor | Commits | Código (LOC) | Tests (LOC) | Docs (LOC) | Días activos | % del código |
|---|---:|---:|---:|---:|---:|---:|
| bchiaramonte-ux | 34 | 2,889 | 124 | 1 | 15 | 100% |

> _El LOC de autoría lo calcula RepoSense sobre el historial y puede diferir del LOC de código del análisis de calidad (que mira solo el estado de `main`)._

## 3. Fallas detectadas

Reúne lo que el análisis marcó y lo que todavía falta para cada semáforo. Cuando la señal se puede asociar a archivos, se indican para orientar la revisión. No es una nota.

| Falla | Tu resultado | Requisito | Archivos |
|---|---|---|---|
| Señales POO/SOLID críticas | Baja cohesión (LCOM), Acoplamiento alto (CBO) | Aprobar: ninguna<br>Destacar: ninguna | `app/src/main/kotlin/com/example/juegofisica/CanvasSimulacionTiroOblicuo.kt`, `app/src/main/kotlin/com/example/juegofisica/PantallaJuegoMRUVBase.kt`, `app/src/main/kotlin/com/example/juegofisica/PantallaSimulacionTiroOblicuo.kt`, `app/src/main/kotlin/com/example/juegofisica/ParametrosTiroOblicuo.kt` |

## 4. Aspectos evaluados

Cada aspecto indica **qué mide**, **por qué importa** y **tu resultado**. Cuando el análisis reportó una señal, se agregan preguntas para **investigar la causa** (no hay recetas: entender el porqué es parte de la evaluación).

### 1. Índice de calidad POO/SOLID (indice)

- **Qué mide:** Un resumen numérico (0-100) que combina la riqueza de clases bien usadas con la penalización por señales POO/SOLID y duplicación, y luego aplica topes duros.
- **Por qué importa:** Ordena y prioriza la revisión: un índice bajo indica varias señales que conviene mirar juntas.
- **Tu resultado:** índice **92/100**

### 2. Duplicación lógica (DRY) (duplicacion) — sin señales

- **Qué mide:** Porcentaje de bloques de LÓGICA repetidos. Quedan fuera imports/includes, anotaciones, declaraciones de campos y comentarios/Javadoc/docstrings: esos bloques son boilerplate, no lógica.
- **Por qué importa:** Cada copia de lógica se mantiene por separado: un arreglo hay que repetirlo y los defectos se multiplican.
- **Tu resultado:** **1.2%** de duplicación lógica

### 3. Adopción de POO (% de código en clases) (poo) — sin señales

- **Qué mide:** Proporción de las **líneas de lógica** que viven dentro de una clase. Quedan fuera `package`/`import`/`using`/anotaciones y comentarios/Javadoc/docstrings: no son código que pueda estar dentro de una clase y, si contaran, el porcentaje quedaría topado por el boilerplate de cada archivo.
- **Por qué importa:** El diseño orientado a objetos es el eje de la materia: el comportamiento debería vivir como método de una clase.
- **Tu resultado:** **97.7%** del código dentro de clases
- **Cómo leerlo:** Es una **proporción**, no un acumulado: agregar código dentro de una clase no la sube por sí solo (numerador y denominador crecen juntos). Sube cuando el código que está **fuera** de clases (funciones sueltas, `main`, constantes de módulo, scripts) pasa a ser método de una clase o deja de existir. La medición se hace sobre `main`: el trabajo que todavía vive en otra rama no se refleja hasta que se integra.

### 4. Funciones fuera de clases (sueltas) — sin señales

- **Qué mide:** Funciones de nivel de módulo que no pertenecen a ninguna clase (en esta materia el comportamiento debe ser método de una clase).
- **Por qué importa:** Son la forma más directa de código procedural en un proyecto que debería ser orientado a objetos.
- **Tu resultado:** **0** función(es) fuera de clases

### 5. Clase demasiado grande (SRP) (god) — sin señales

- **Qué mide:** Clases con demasiadas líneas o métodos de LÓGICA. No cuentan los getters/setters triviales ni la cantidad de campos por sí sola.
- **Por qué importa:** Una clase que hace de todo cambia por muchos motivos y cuesta entenderla y probarla.
- **Tu resultado:** **0** clase(s) demasiado grande(s)

### 6. Baja cohesión (LCOM) (cohesion) — con señales

- **Qué mide:** Métodos y campos de una clase que casi no se relacionan entre sí.
- **Por qué importa:** Una unidad que cambia por motivos distintos y comparte poca información suele esconder más de una responsabilidad.
- **Tu resultado:** **3** clase(s) con baja cohesión
- **Para investigar la causa:**
    - ¿Qué métodos de la clase no comparten estado con el resto?
    - ¿Cambian por motivos distintos según la tarea?
    - ¿Se pensó su responsabilidad antes de escribirla, o se fue agregando?

### 7. Complejidad alta (complejidad) — sin señales

- **Qué mide:** Complejidad ciclomática: cantidad de caminos posibles dentro de un método o función (condicionales, bucles, casos).
- **Por qué importa:** Muchos caminos = difícil de seguir y de probar; suelen esconderse casos sin cubrir.
- **Tu resultado:** **0** método(s)/función(es) con complejidad alta

### 8. Métodos/funciones largos o con muchos parámetros (tamano) — sin señales

- **Qué mide:** Tamaño de los métodos/funciones y cantidad de parámetros.
- **Por qué importa:** Una función que hace varias cosas cuesta leerla, nombrarla y reutilizarla.
- **Tu resultado:** **0** método(s)/función(es) largos o con muchos parámetros

### 9. Acoplamiento alto (CBO) (acoplamiento) — con señales

- **Qué mide:** Cuánto depende una clase de otros tipos del proyecto. No cuentan los tipos de la plataforma (biblioteca estándar, DOM, motor Godot) ni los métodos/propiedades propias.
- **Por qué importa:** Piezas muy enlazadas hacen que un cambio se propague y que sea difícil reemplazar partes.
- **Tu resultado:** **1** clase(s) muy acoplada(s)
- **Para investigar la causa:**
    - ¿La clase conoce detalles internos de muchas otras?
    - ¿Se puede cambiar una parte sin tocar las demás?
    - ¿Los colaboradores llegan como parámetros o se crean por dentro?

### 10. Herencia profunda (DIT) (herencia) — sin señales

- **Qué mide:** Profundidad de la cadena de herencia de una clase.
- **Por qué importa:** Jerarquías muy profundas vuelven difícil saber de dónde viene cada comportamiento.
- **Tu resultado:** **0** clase(s) con herencia profunda

### 11. Switch sobre tipos (OCP) (ocp) — sin señales

- **Qué mide:** Cadenas de dispatch por tipo (`else if ... is` / `else if ... instanceof`, `switch` sobre tipo o `typeof`). Un `is Tipo variable` aislado (pattern matching / cast seguro) NO cuenta como switch.
- **Por qué importa:** Cada tipo nuevo obliga a tocar varios lugares, en vez de extender con una implementación nueva.
- **Tu resultado:** **0** clase(s) con switch sobre tipos

### 12. Interfaz con demasiados métodos (ISP) (isp) — sin señales

- **Qué mide:** Interfaces o clases abstractas con muchos métodos declarados.
- **Por qué importa:** Obliga a implementar operaciones que no se usan y acopla a los clientes a métodos que no necesitan.
- **Tu resultado:** **0** interfaz(es) con demasiados métodos

### 13. Override vacío o 'no implementado' (LSP) (lsp) — sin señales

- **Qué mide:** Métodos sobrescritos que quedan vacíos o lanzan 'no implementado'.
- **Por qué importa:** Suele indicar que la jerarquía no representa bien el problema y rompe la sustitución entre clases.
- **Tu resultado:** **0** override(s) vacío(s) o 'no implementado'

### 14. Clase sin métodos (solo datos) (data) — sin señales

- **Qué mide:** Clases que solo acumulan campos y no tienen comportamiento.
- **Por qué importa:** Separar datos de comportamiento reparte por el proyecto la lógica que debería estar cerca de esos datos.
- **Tu resultado:** **0** clase(s) sin métodos (solo datos)

### 15. Campos mutables públicos (encapsulación) (encapsulacion) — sin señales

- **Qué mide:** Clases que exponen estado **modificable** desde afuera. No cuentan constantes (`static final`, `const`), campos `final`/`readonly`, propiedades `val` de Kotlin ni propiedades con `private set`.
- **Por qué importa:** Expone el estado interno y hace que cualquier cambio dependa de quién lo modifica desde afuera.
- **Tu resultado:** **0** clase(s) con campos mutables públicos

### 16. Clase solo con métodos estáticos (utilitaria) (static) — sin señales

- **Qué mide:** Clases donde todos los métodos son estáticos.
- **Por qué importa:** Es comportamiento procedural disfrazado de clase: no se instancia ni participa del modelo de objetos.
- **Tu resultado:** **0** clase(s) solo con métodos estáticos

### Archivos donde se originan las señales

> Se listan **9** hallazgo(s) localizados en **5** archivo(s). La columna `Línea` es la línea del archivo donde el análisis detectó la señal; `—` indica una señal de nivel repositorio (sin archivo propio).

| Aspecto | Archivo | Línea | Hallazgo |
|---|---|---:|---|
| Duplicación lógica (DRY) | — | — | duplicación lógica: 1.2% · boilerplate: 0.0% |
| Duplicación lógica (DRY) | `app/src/main/kotlin/com/example/juegofisica/PantallaDerrotaMRUV.kt` | 22 | bloque de 8 líneas repetido 2 veces en app/src/main/kotlin/com/example/juegofisica/PantallaDerrotaMRUV.kt:22, app/src/main/kotlin/com/example/juegofisica/CuadroReglas.kt:41 |
| Duplicación lógica (DRY) | `app/src/main/kotlin/com/example/juegofisica/PantallaDerrotaMRUV.kt` | 27 | bloque de 8 líneas repetido 2 veces en app/src/main/kotlin/com/example/juegofisica/PantallaDerrotaMRUV.kt:27, app/src/main/kotlin/com/example/juegofisica/CuadroReglas.kt:46 |
| Duplicación lógica (DRY) | `app/src/main/kotlin/com/example/juegofisica/PantallaDerrotaMRUV.kt` | 32 | bloque de 8 líneas repetido 2 veces en app/src/main/kotlin/com/example/juegofisica/PantallaDerrotaMRUV.kt:32, app/src/main/kotlin/com/example/juegofisica/CuadroReglas.kt:51 |
| Duplicación lógica (DRY) | `app/src/main/kotlin/com/example/juegofisica/PantallaDerrotaMRUV.kt` | 33 | bloque de 8 líneas repetido 2 veces en app/src/main/kotlin/com/example/juegofisica/PantallaDerrotaMRUV.kt:33, app/src/main/kotlin/com/example/juegofisica/CuadroReglas.kt:52 |
| Baja cohesión (LCOM) | `app/src/main/kotlin/com/example/juegofisica/CanvasSimulacionTiroOblicuo.kt` | 18 | clase 'CanvasSimulacionTiroOblicuo' con baja cohesión (42%) |
| Baja cohesión (LCOM) | `app/src/main/kotlin/com/example/juegofisica/PantallaSimulacionTiroOblicuo.kt` | 21 | clase 'PantallaSimulacionTiroOblicuo' con baja cohesión (33%) |
| Baja cohesión (LCOM) | `app/src/main/kotlin/com/example/juegofisica/ParametrosTiroOblicuo.kt` | 12 | clase 'ParametrosTiroOblicuo' con baja cohesión (29%) |
| Acoplamiento alto (CBO) | `app/src/main/kotlin/com/example/juegofisica/PantallaJuegoMRUVBase.kt` | 17 | clase 'PantallaJuegoMRUVBase' muy acoplada (CBO=12) |

### Desglose de la columna S (SRP)

> La columna **S** no es un puntaje: cuenta **señales** de SRP. Aquí suma **3** entrada(s) sobre **3** clase(s) distinta(s).

| Señal | Entradas | Regla (perfil exigente) |
|---|---:|---|
| Cohesión baja | 3 | < 50 % de los pares de métodos comparten algún campo (con ≥ 4 métodos con lógica y ≥ 3 campos). |

**Clases con baja cohesión (3)**

- `ParametrosTiroOblicuo`: cohesión baja (29% de pares comparten campos)
- `PantallaSimulacionTiroOblicuo`: cohesión baja (33% de pares comparten campos)
- `CanvasSimulacionTiroOblicuo`: cohesión baja (42% de pares comparten campos)

> El análisis no excluye tests: una clase de test que supere los umbrales también suma.

## 5. Evolución por commit

Métricas calculadas sobre el estado de `main` en **cada commit** (41 commits con trabajo), del primero al último. La calidad usa el perfil `exigente`; el aporte de código lo mide RepoSense. El índice puede **bajar** al crecer el proyecto: se acumulan señales.

```mermaid
xychart-beta
    title "Índice POO/SOLID a lo largo de los commits"
    x-axis ["812737a", "b0e9f11", "4e77849", "fe8f0b8", "733ab8b", "2fc54b0", "deb99ac", "707ed38", "3e681d2", "657d351", "3b5d59e", "7d2dc83", "39d0907", "3a3b787", "cbdf74c", "51ecf43", "403f42c", "8be2ade", "ffa096b", "2086580", "3077384", "ccc7b07", "4231b28", "d28ecf9", "bf0bd73", "acdf5ef", "cbdf280", "9e7b58a", "7aff765", "c12aadb", "37b3af8", "cf28966", "97cf8d2", "70425b7", "94beb2c", "c30c85b", "2635230", "5475378", "5be0bc6", "51f3746", "bf3e0d9"]
    y-axis "Índice" 0 --> 100
    line [0, 0, 60, 10, 45, 43, 34, 32, 14, 15, 13, 13, 11, 21, 19, 5, 14, 20, 31, 29, 34, 29, 38, 50, 45, 38, 36, 38, 38, 36, 53, 53, 53, 53, 53, 53, 53, 53, 92, 92, 92]
```

| # | Fecha | Commit | Autor | LOC | Índice | %POO | Duplic. | Sueltas |
|---|---|---|---|---:|---:|---:|---:|---:|
| 1 | 2026-08-14 | `812737a` | bchiaramonte-ux | 0 | 0 | 0% | 0.0% | 0 |
| 2 | 2026-08-24 | `b0e9f11` | dqmdz-etec | 0 | 0 | 0% | 0.0% | 0 |
| 3 | 2026-08-31 | `4e77849` | bchiaramonte-ux | 171 | 60 | 60% | 0.0% | 0 |
| 4 | 2026-09-04 | `fe8f0b8` | bchiaramonte-ux | 330 | 10 | 80% | 34.3% | 0 |
| 5 | 2026-09-04 | `733ab8b` | bchiaramonte-ux | 343 | 45 | 80% | 12.3% | 0 |
| 6 | 2026-09-07 | `2fc54b0` | bchiaramonte-ux | 460 | 43 | 85% | 16.2% | 0 |
| 7 | 2026-09-07 | `deb99ac` | bchiaramonte-ux | 566 | 34 | 88% | 20.8% | 0 |
| 8 | 2026-09-28 | `707ed38` | bchiaramonte-ux | 543 | 32 | 88% | 21.9% | 0 |
| 9 | 2026-10-02 | `3e681d2` | bchiaramonte-ux | 734 | 14 | 90% | 32.2% | 0 |
| 10 | 2026-10-02 | `657d351` | bchiaramonte-ux | 650 | 15 | 89% | 32.6% | 0 |
| 11 | 2026-10-05 | `3b5d59e` | bchiaramonte-ux | 650 | 13 | 89% | 33.9% | 0 |
| 12 | 2026-10-05 | `7d2dc83` | bchiaramonte-ux | 655 | 13 | 89% | 33.7% | 0 |
| 13 | 2026-10-05 | `39d0907` | bchiaramonte-ux | 732 | 11 | 90% | 35.6% | 0 |
| 14 | 2026-10-05 | `3a3b787` | bchiaramonte-ux | 851 | 21 | 92% | 28.8% | 0 |
| 15 | 2026-10-09 | `cbdf74c` | bchiaramonte-ux | 938 | 19 | 93% | 31.2% | 0 |
| 16 | 2026-10-09 | `51ecf43` | bchiaramonte-ux | 1,005 | 5 | 93% | 39.9% | 0 |
| 17 | 2026-10-09 | `403f42c` | bchiaramonte-ux | 1,006 | 14 | 93% | 34.7% | 0 |
| 18 | 2026-10-09 | `8be2ade` | bchiaramonte-ux | 1,068 | 20 | 94% | 27.6% | 0 |
| 19 | 2026-10-09 | `ffa096b` | bchiaramonte-ux | 1,317 | 31 | 95% | 21.9% | 0 |
| 20 | 2026-10-16 | `2086580` | bchiaramonte-ux | 1,326 | 29 | 95% | 23.0% | 0 |
| 21 | 2026-10-16 | `3077384` | bchiaramonte-ux | 1,583 | 34 | 96% | 22.0% | 0 |
| 22 | 2026-10-16 | `ccc7b07` | bchiaramonte-ux | 1,679 | 29 | 96% | 24.3% | 0 |
| 23 | 2026-10-19 | `4231b28` | bchiaramonte-ux | 1,998 | 38 | 97% | 22.4% | 0 |
| 24 | 2026-10-30 | `d28ecf9` | bchiaramonte-ux | 2,622 | 50 | 98% | 12.7% | 0 |
| 25 | 2026-10-30 | `bf0bd73` | bchiaramonte-ux | 2,751 | 45 | 98% | 15.1% | 0 |
| 26 | 2026-10-30 | `acdf5ef` | bchiaramonte-ux | 2,690 | 38 | 98% | 19.0% | 0 |
| 27 | 2026-11-02 | `cbdf280` | bchiaramonte-ux | 2,802 | 36 | 98% | 18.2% | 0 |
| 28 | 2026-11-02 | `9e7b58a` | bchiaramonte-ux | 2,730 | 38 | 98% | 17.6% | 0 |
| 29 | 2026-11-06 | `7aff765` | bchiaramonte-ux | 3,430 | 38 | 98% | 13.9% | 0 |
| 30 | 2026-11-06 | `c12aadb` | bchiaramonte-ux | 3,482 | 36 | 98% | 13.7% | 0 |
| 31 | 2026-11-06 | `37b3af8` | bchiaramonte-ux | 3,226 | 53 | 98% | 4.5% | 0 |
| 32 | 2026-11-07 | `cf28966` | dqmdz-etec | 3,226 | 53 | 98% | 4.5% | 0 |
| 33 | 2026-11-07 | `97cf8d2` | dqmdz-etec | 3,226 | 53 | 98% | 4.5% | 0 |
| 34 | 2026-11-07 | `70425b7` | dqmdz-etec | 3,226 | 53 | 98% | 4.5% | 0 |
| 35 | 2026-11-07 | `94beb2c` | dqmdz-etec | 3,226 | 53 | 98% | 4.5% | 0 |
| 36 | 2026-11-13 | `c30c85b` | dqmdz-etec | 3,226 | 53 | 98% | 4.5% | 0 |
| 37 | 2026-11-14 | `2635230` | dqmdz-etec | 3,226 | 53 | 98% | 4.5% | 0 |
| 38 | 2026-11-16 | `5475378` | dqmdz-etec | 3,226 | 53 | 98% | 4.5% | 0 |
| 39 | 2026-11-16 | `5be0bc6` | bchiaramonte-ux | 2,616 | 92 | 98% | 1.2% | 0 |
| 40 | 2026-11-17 | `51f3746` | dqmdz-etec | 2,616 | 92 | 98% | 1.2% | 0 |
| 41 | 2026-11-20 | `bf3e0d9` | dqmdz-etec | 2,616 | 92 | 98% | 1.2% | 0 |

---
_Informe generado por la cátedra. Los criterios y umbrales de medición corresponden al perfil `exigente` de `calidad.py`; el semáforo de aprobación, a `CRITERIOS-CALIDAD.md` (ver `README.md`). Ante dudas, revisá el archivo señalado._
<!-- evaluacion:fin -->
