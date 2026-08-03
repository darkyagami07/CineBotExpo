package logica;

import datos.GestorCatalogo;
import models.Pelicula;
import java.util.HashSet;
import java.util.List;

public class MotorRecomendaciones {

    private HashSet<Integer> memoriaActiva; // Se guarda por ID para ser exactos

    public MotorRecomendaciones() {
        this.memoriaActiva = new HashSet<>();
    }

    public String buscarMejorPelicula(String textoProcesado) {
        String[] palabrasUsuario = textoProcesado.split("\\s+");
        
        // Se obtiene la lista completa de peliculas desde el CSV
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

            if (puntosActuales > maxPuntos) {
                maxPuntos = puntosActuales;
                mejorPelicula = peli;
            }
        }

        if (maxPuntos > 0 && mejorPelicula != null) {
            memoriaActiva.add(mejorPelicula.getId());
            return "Te recomiendo ver '" + mejorPelicula.getTitulo() + "'! (Género: " + mejorPelicula.getGenero() + " | Coincidencias: " + maxPuntos + ")";
        } else {
            return "No encontré opciones nuevas para lo que buscas o ya te recomendé las películas disponibles en esa categoría.";
        }
    }

    public void limpiarMemoria() {
        memoriaActiva.clear();
    }
}