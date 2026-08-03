package logica;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class Pelicula {
    String titulo;
    String[] palabrasClave;

    public Pelicula(String titulo, String[] palabrasClave) {
        this.titulo = titulo;
        this.palabrasClave = palabrasClave;
    }
}

public class MotorRecomendaciones {

    private List<Pelicula> catalogo;
    
    private HashSet<String> memoriaActiva;

    public MotorRecomendaciones() {
        this.catalogo = new ArrayList<>();
        this.memoriaActiva = new HashSet<>();
        cargarCatalogo();
    }

    private void cargarCatalogo() {
        catalogo.add(new Pelicula("Matrix", new String[]{"accion", "ciencia", "ficcion", "hacker"}));
        catalogo.add(new Pelicula("El Conjuro", new String[]{"terror", "miedo", "fantasmas", "suspenso"}));
        catalogo.add(new Pelicula("Superbad", new String[]{"comedia", "risa", "divertido", "humor"}));
    }

    public String buscarMejorPelicula(String textoProcesado) {
        String[] palabrasUsuario = textoProcesado.split(" ");

        Pelicula mejorPelicula = null;
        int maxPuntos = 0;

        for (Pelicula peli : catalogo) {

            if (memoriaActiva.contains(peli.titulo)) {
                continue;
            }

            int puntosActuales = 0;
            for (String palabraClave : peli.palabrasClave) {
                for (String palabraUsuario : palabrasUsuario) {
                    if (palabraUsuario.equalsIgnoreCase(palabraClave)) {
                        puntosActuales++;
                    }
                }
            }

            if (puntosActuales > maxPuntos) {
                maxPuntos = puntosActuales;
                mejorPelicula = peli;
            }
        }

        if (maxPuntos > 0 && mejorPelicula != null) {
            memoriaActiva.add(mejorPelicula.titulo);
            return "Te recomiendo ver '" + mejorPelicula.titulo + "'! (Coincidencias: " + maxPuntos + ")";
        } else {
            return "No encontré opciones nuevas para lo que buscas o ya te recomendé las películas disponibles en esa categoría.";
        }
    }
}