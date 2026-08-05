package logica;

import datos.GestorCatalogo;
import models.Pelicula;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class MotorRecomendaciones {

    private HashSet<Integer> memoriaActiva; // Se guarda por ID para ser exactos

    private Pelicula ultimaRecomendada;
    private String ultimoContextoExitoso = "";
    public MotorRecomendaciones() {
        this.memoriaActiva = new HashSet<>();
        this.ultimaRecomendada = null;
    }

  public String buscarMejorPelicula(String textoProcesado) {
        // MAGIA IA: Si el usuario pide "otra" y tenemos memoria, fusionamos las frases
      if ((textoProcesado.contains("otra") || textoProcesado.contains("mas") || textoProcesado.contains("siguiente")
             || textoProcesado.contains("no") || textoProcesado.contains("gusta") || textoProcesado.contains("cambia") || textoProcesado.contains("mala")) 
             && !ultimoContextoExitoso.isEmpty()) {
             
            // Si el usuario rechaza la película, reciclamos el contexto anterior para buscar otra
            textoProcesado = textoProcesado + " " + ultimoContextoExitoso;
        }

        String[] palabrasUsuario = textoProcesado.split("\\s+");
    // Se obtiene la lista completa de películas desde el catálogo
    // Asegúrate de tener importada la clase GestorCatalogo correctamente
    List<Pelicula> catalogo = GestorCatalogo.getInstancia().getCatalogoPeliculas();

    Pelicula mejorPelicula = null;
    int maxPuntos = 0;

    for (Pelicula peli : catalogo) {
        // Memoria activa: ignora si ya se recomendó en la sesión
        if (memoriaActiva.contains(peli.getId())) {
            continue;
        }

        int puntosActuales = 0;
        for (String palabraClave : peli.getPalabrasClave()) {
            for (String palabraUsuario : palabrasUsuario) {
                if (palabraUsuario.equalsIgnoreCase(palabraClave)) {
                    puntosActuales++;
                }
            }
        }

        // EVALUACIÓN DE PUNTOS Y CRITERIO DE DESEMPATE
        if (puntosActuales > maxPuntos) {
            maxPuntos = puntosActuales;
            mejorPelicula = peli;
        } else if (puntosActuales == maxPuntos && maxPuntos > 0) {
            // CRITERIO DE DESEMPATE EXPLÍCITO: Si empatan en coincidencias, gana la del ID mayor
            if (peli.getId() > mejorPelicula.getId()) {
                mejorPelicula = peli;
            }
        }
    }

    // RESPUESTA FINAL
    if (maxPuntos > 0 && mejorPelicula != null) {
        memoriaActiva.add(mejorPelicula.getId());
        this.ultimaRecomendada = mejorPelicula;
        this.ultimoContextoExitoso = textoProcesado; 
        
        // --- MAGIA IA: Generación Dinámica de Lenguaje (NLG) ---
        String[] introducciones = {
            "¡Tengo la opción perfecta! Te recomiendo ",
            "Pensando en lo que me dices, creo que disfrutarás de ",
            "Me parece que esta historia encaja muy bien: ",
            "Para lo que buscas, definitivamente te sugiero ",
            "¡Esa la tengo clara! Deberías ver "
        };
        
        String[] conectores = {
            ". ",
            ". Te cuento un poco: ",
            ". Prepara las cotufas porque... ",
            ". Te va a atrapar porque: "
        };
        
        Random rand = new Random();
        String introAleatoria = introducciones[rand.nextInt(introducciones.length)];
        String conectorAleatorio = conectores[rand.nextInt(conectores.length)];
        
        // Armamos la oración combinando las piezas al azar con los datos de la película
        return introAleatoria + "'" + mejorPelicula.getTitulo() + "'" + conectorAleatorio + mejorPelicula.getMensajeBot();
        
    } else {
        // --- MAGIA IA: Filtro de Entradas Irrelevantes (Fallback) ---
        
        // 1. Detectamos si el usuario ingresó números o símbolos matemáticos (+, -, *, /)
        if (textoProcesado.matches(".*\\d.*") || textoProcesado.matches(".*[+\\-*/].*")) {
            return "¡Ups! Mi núcleo de procesamiento está calibrado exclusivamente para el séptimo arte. No resuelvo operaciones matemáticas ni consultas generales. ¡Pero pregúntame de cine! ¿Qué género te gustaría ver?";
        }
        
        // 2. Mensajes dinámicos para cuando el usuario dice algo que no tiene nada que ver con cine
        String[] fueraDeContexto = {
            "No logré identificar una emoción o temática en tu mensaje. Recuerda que soy una IA especializada únicamente en recomendar películas. ¿De qué humor estás hoy?",
            "Mi base de datos no encontró coincidencias con eso. Como asistente cinematográfico, mi misión es hablar de cine. ¿Buscamos alguna película?",
            "Parece que nos salimos del tema, o tal vez ya te recomendé todas las películas de esa categoría. ¡Volvamos al cine! ¿Qué otra historia te gustaría explorar?"
        };
        
        Random rand = new Random();
        return fueraDeContexto[rand.nextInt(fueraDeContexto.length)];
    }
}
    public Pelicula getUltimaRecomendada() {
        return ultimaRecomendada;
    }
    public void limpiarMemoria() {
        memoriaActiva.clear();
    }
}