package logica;

import datos.GestorCatalogo;
import models.Pelicula;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

public class MotorRecomendaciones {

    private HashSet<Integer> memoriaActiva; // Guarda IDs de películas ya recomendadas
    private Pelicula ultimaRecomendada;
    private String ultimoContextoExitoso;

    public MotorRecomendaciones() {
        this.memoriaActiva = new HashSet<>();
        this.ultimaRecomendada = null;
        this.ultimoContextoExitoso = "";
    }

    public String buscarMejorPelicula(String textoProcesado) {
        if (textoProcesado == null || textoProcesado.trim().isEmpty()) {
            return "No recibí ningún texto. ¿Qué tipo de película buscas hoy?";
        }

        // Reutilización de contexto si el usuario pide "otra", "siguiente" o rechaza la opción anterior
        if ((textoProcesado.contains("otra") || textoProcesado.contains("mas") || textoProcesado.contains("siguiente")
                || textoProcesado.contains("no") || textoProcesado.contains("gusta") || textoProcesado.contains("cambia") 
                || textoProcesado.contains("mala")) && !ultimoContextoExitoso.isEmpty()) {
                
            textoProcesado = textoProcesado + " " + ultimoContextoExitoso;
        }

        String[] palabrasUsuario = textoProcesado.toLowerCase().split("\\s+");
        List<Pelicula> catalogo = GestorCatalogo.getInstancia().getCatalogoPeliculas();
        Map<String, String> diccionarioPLN = GestorCatalogo.getInstancia().getDiccionarioSinonimos();

        List<Pelicula> candidatos = new ArrayList<>();
        Pelicula mejorPelicula = null;
        int maxPuntos = 0;

        for (Pelicula peli : catalogo) {
            // Ignorar películas ya recomendadas en la sesión activa
            if (memoriaActiva.contains(peli.getId())) {
                continue;
            }

            int puntosActuales = 0;
            
            // Puntos extra si el usuario menciona directamente un género de la película
            Set<String> generosPeli = obtenerGenerosNormalizados(peli, diccionarioPLN);
            for (String generoOficial : generosPeli) {
                if (!generoOficial.isEmpty() && textoProcesado.matches(".*\\b" + Pattern.quote(generoOficial) + "\\b.*")) {
                    puntosActuales += 3;
                }
            }

            // Comparación mejorada y flexible para palabras clave emocionales/temáticas
            for (String palabraClave : peli.getPalabrasClave()) {
                String claveLimpia = palabraClave.trim().toLowerCase();
                if (claveLimpia.isEmpty()) continue;

                for (String palabraUsuario : palabrasUsuario) {
                    String uLimpia = palabraUsuario.trim();
                    if (uLimpia.isEmpty()) continue;

                    // Coincidencia exacta o contenida (para evitar fallos por subcadenas)
                    if (uLimpia.equalsIgnoreCase(claveLimpia) || 
                        claveLimpia.contains(uLimpia) || 
                        uLimpia.contains(claveLimpia)) {
                        puntosActuales++;
                    }
                }
            }

            // Selección por mayor puntuación
            if (puntosActuales > maxPuntos) {
                maxPuntos = puntosActuales;
                candidatos.clear();
                candidatos.add(peli);
            } else if (puntosActuales == maxPuntos && puntosActuales > 0) {
                candidatos.add(peli);
            }
        }

        // Respuesta con coincidencia exitosa
        if (maxPuntos > 0 && !candidatos.isEmpty()) {
            Random rand = new Random();
            mejorPelicula = candidatos.get(rand.nextInt(candidatos.size()));
            memoriaActiva.add(mejorPelicula.getId());
            this.ultimaRecomendada = mejorPelicula;
            this.ultimoContextoExitoso = textoProcesado;

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

            String introAleatoria = introducciones[rand.nextInt(introducciones.length)];
            String conectorAleatorio = conectores[rand.nextInt(conectores.length)];

            return introAleatoria + "'" + mejorPelicula.getTitulo() + "'" + conectorAleatorio + mejorPelicula.getMensajeBot();

        } else {
            // Filtro para operaciones matemáticas o números
            if (textoProcesado.matches(".*\\d.*") || textoProcesado.matches(".*[+\\-*/].*")) {
                return "¡Ups! Mi núcleo de procesamiento está calibrado exclusivamente para el séptimo arte. No resuelvo operaciones matemáticas ni consultas generales. ¡Pero pregúntame de cine! ¿Qué género te gustaría ver?";
            }

            // Fallback
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
        ultimaRecomendada = null;
        ultimoContextoExitoso = "";
    }

    private static Set<String> obtenerGenerosNormalizados(Pelicula pelicula, Map<String, String> diccionarioPLN) {
        Set<String> generos = new HashSet<>();
        if (pelicula == null || pelicula.getGenero() == null || pelicula.getGenero().trim().isEmpty()) {
            return generos;
        }

        String[] partesGenero = pelicula.getGenero().toLowerCase().split("[/\\\\|,]");
        for (String parte : partesGenero) {
            String generoNormalizado = normalizarGenero(parte, diccionarioPLN);
            if (!generoNormalizado.isEmpty()) {
                generos.add(generoNormalizado);
            }
        }
        return generos;
    }

    private static String normalizarGenero(String genero, Map<String, String> diccionarioPLN) {
        if (genero == null || genero.trim().isEmpty()) {
            return "";
        }

        String clave = genero.trim().toLowerCase();
        if (diccionarioPLN != null && diccionarioPLN.containsKey(clave)) {
            return diccionarioPLN.get(clave);
        }
        return clave;
    }
}