package logica;

import datos.GestorCatalogo;
import models.Pelicula;
import java.util.ArrayList;
import java.util.Arrays;
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

        // Detectar consultas fuera de contexto (matemáticas, definiciones, fechas, clima, etc.)
        String textoMinusculas = textoProcesado.toLowerCase();
        if (textoMinusculas.matches(".*\\b(cuanto|cuánto|que es|qué es|definici[oó]n|que dia|qué dia|fecha|hora|clima|tiempo|temperatura|quien es|quién es|capital de|como llegar|cómo llegar)\\b.*")
                || textoMinusculas.matches(".*\\d+\\s*[+\\-*/]\\s*\\d+.*")
                || textoMinusculas.matches(".*[+\\-*/].*")) {
            return "Lo siento, solo soy un recomendador de películas; puedo ayudar con emociones, géneros y recomendaciones.";
        }

        String[] palabrasUsuario = textoProcesado.toLowerCase().split("\\s+");
        List<Pelicula> catalogo = GestorCatalogo.getInstancia().getCatalogoPeliculas();
        Map<String, String> diccionarioPLN = GestorCatalogo.getInstancia().getDiccionarioSinonimos();

        // Detectar emociones presentes en el texto procesado (incluye 'soledad')
        Set<String> emocionesDetectadas = new HashSet<>();
        String[] emocionesPosibles = {"alegria", "tristeza", "miedo", "ira", "asco", "sorpresa", "soledad"};
        for (String emo : emocionesPosibles) {
            if (textoProcesado.matches(".*\\b" + Pattern.quote(emo) + "\\b.*")) {
                emocionesDetectadas.add(emo);
            }
        }

        // Construir conjunto de géneros prohibidos según emoción detectada
        Set<String> generosProhibidos = new HashSet<>();
        for (String emo : emocionesDetectadas) {
            if (emo.equals("tristeza") || emo.equals("soledad")) {
                generosProhibidos.addAll(Arrays.asList("terror", "suspenso", "gore"));
            } else if (emo.equals("miedo")) {
                generosProhibidos.addAll(Arrays.asList("terror", "horror"));
            }
        }

        // Si el usuario pide explícitamente un género, respetarlo (no filtrar)
        List<String> generosOficiales = Arrays.asList("accion", "comedia", "romance", "terror", "drama", "fantasia", "animacion", "suspenso", "documental");
        boolean generoExplicito = false;
        for (String g : generosOficiales) {
            if (textoProcesado.matches(".*\\b" + Pattern.quote(g) + "\\b.*")) {
                generoExplicito = true;
                break;
            }
        }

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
            // Filtro para operaciones matemáticas o preguntas tipo "¿cuánto es 1+1?"
            if (textoProcesado.matches(".*\\b(cuanto|cuánto)\\b.*\\d.*") ||
                textoProcesado.matches(".*\\d+\\s*[+\\-*/]\\s*\\d+.*") ||
                textoProcesado.matches(".*[+\\-*/].*")) {
                return "Lo siento, solo soy un recomendador de películas, no una calculadora.";
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