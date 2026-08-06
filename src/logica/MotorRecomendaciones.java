package logica;

import datos.GestorCatalogo;
import models.Pelicula;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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

        Pelicula mejorPelicula = null;
        int maxPuntos = 0;

        for (Pelicula peli : catalogo) {
            // Ignorar películas ya recomendadas en la sesión activa
            if (memoriaActiva.contains(peli.getId())) {
                continue;
            }

            int puntosActuales = 0;
            
            // Comparación mejorada y flexible
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
                mejorPelicula = peli;
            } else if (puntosActuales == maxPuntos && maxPuntos > 0 && mejorPelicula != null) {
                // Desempate por ID más reciente
                if (peli.getId() > mejorPelicula.getId()) {
                    mejorPelicula = peli;
                }
            }
        }

        // Respuesta con coincidencia exitosa
        if (maxPuntos > 0 && mejorPelicula != null) {
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

            Random rand = new Random();
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
}