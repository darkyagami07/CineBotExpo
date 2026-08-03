# Guía del Proyecto Integrador: Chatbot Recomendador "CineBot" (V3)

## 1. Descripción del Proyecto

El proyecto **"CineBot"** consiste en desarrollar una aplicación de consola en Java que interactúa con el usuario para recomendarle películas o series en función de su estado de ánimo, gustos y contexto conversacional.

El sistema operará bajo el paradigma de un **Agente de Inteligencia Artificial con Memoria Limitada**. El bot integra un módulo de **Procesamiento de Lenguaje Natural (PLN)** enriquecido con un **Diccionario de Sinónimos** para garantizar coincidencias (*match*) efectivas incluso cuando el usuario use términos equivalentes a las palabras clave del catálogo. 

Además, capturará datos demográficos básicos (**Nombre, Género y Año de Nacimiento**) y los registrará de manera persistente en un archivo de salida (`usuarios_preferencias.csv`) para generar análisis estadísticos.

---

## 2. Requerimientos Técnicos

### Requerimientos de Inteligencia Artificial y Analítica

- **Paradigma:** Agente con Memoria Limitada (mantiene historial reciente dentro de la sesión para no repetir recomendaciones).
- **PLN y Expansión por Sinónimos:**
  - Limpieza y normalización de entradas (minúsculas, remoción de signos de puntuación).
  - **Mapeo de Sinónimos:** El bot cargará una lista/diccionario de equivalencias (ej. *"deprimido"*, *"bajoneado"* o *"agüitado"* se traducen o asocian a *"triste"*). Si el usuario utiliza un sinónimo, el sistema debe mapearlo a la palabra clave oficial para no perder el match.
- **Motor de Inferencia:**
  - Algoritmo de puntuación por coincidencias de palabras clave de cada película.
  - Criterio de desempate en caso de puntuaciones iguales.
- **Módulo Estadístico:**
  - Procesamiento del archivo `usuarios_preferencias.csv` para generar reportes (porcentajes por género, promedio de edad, temas/géneros más buscados).

### Requerimientos de Infraestructura y Programación (Java)

- **Estructura del Proyecto (Paquetes):**
  - `models`: Clases `Pelicula` y `Usuario`.
  - `datos`: Manejo de archivos y persistencia (`GestorCatalogo`, `GestorPersistencia`).
  - `logica`: `ProcesadorPLN`, `MotorRecomendaciones` / `MotorInferencia`.
  - `interfaz`: `Registro` (Consola y ciclo del Chat).
  - `reportes`: `EstadisticasServicio`.
- **Patrones de Diseño:** Patrón Singleton para `GestorCatalogo`.
- **Persistencia en Disco (E/S):**
  - Lectura de archivos CSV (`peliculas.csv`, `sinonimos.csv`) mediante `BufferedReader`.
  - Escritura de nuevos usuarios en `usuarios_preferencias.csv` mediante `BufferedWriter`.
- **Manejo de Excepciones:** Bloques `try-catch` para blindar el sistema ante posibles errores de lectura/escritura o formato de datos.

---

## 3. Fases de Desarrollo (Cronograma de Trabajo)

### Fase 1: Arquitectura y Persistencia de Entrada/Salida (Semanas 1-2)
- Estructuración del proyecto en paquetes: `models`, `datos`, `logica`, `interfaz`, `reportes`.
- Creación de las clases modelo: `Pelicula` y `Usuario`.
- Implementación del módulo de captura de datos de usuario (Nombre, Género, Año de Nacimiento).
- Lectura de `peliculas.csv` y `sinonimos.csv` mediante `BufferedReader`.

### Fase 2: Colecciones, Escritura y Patrón Singleton (Semanas 3-4)
- Implementación del patrón Singleton para la gestión del catálogo y del diccionario de sinónimos.
- Carga del diccionario de sinónimos en un `HashMap<String, String>` (`Sinónimo` → `Palabra Clave`).
- Desarrollo de `GestorPersistencia` para el registro continuo en `usuarios_preferencias.csv`.

### Fase 3: Motor de Inferencia, PLN y Memoria Activa (Semanas 5-6)
- Programación de la lógica PLN: sustitución/traducción de palabras usando el `HashMap` de sinónimos previo a la búsqueda de coincidencias.
- Cálculo de puntuaciones de coincidencia con las palabras clave oficiales de la película.
- Integración de `HashSet` para la memoria activa de la sesión (evitar recomendaciones repetidas).
- Bucle interactivo de conversación en consola.

### Fase 4: Módulo Estadístico, Excepciones y Cierre (Semanas 7-8)
- Desarrollo de la clase `EstadisticasServicio` para procesar métricas del archivo de usuarios.
- Menú de consulta de reportes en consola.
- Manejo integral de excepciones `try-catch`.
- Pruebas finales e integración general del proyecto para la Expoferia.