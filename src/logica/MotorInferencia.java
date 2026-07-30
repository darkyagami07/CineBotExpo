package logica;

import datos.GestorCatalogo;
import models.Pelicula;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MotorInferencia {
    private GestorCatalogo catalogo;
    private Set<Integer> memoriaActivaSesion; // Guarda los IDs de películas ya recomendadas para no repetir

    public MotorInferencia() {
        this.catalogo = GestorCatalogo.getInstancia();
        this.memoriaActivaSesion = new HashSet<>();
    }

    // Método preliminar para limpiar la entrada del usuario (PLN)
    public String normalizarTexto(String texto) {
        if (texto == null) return "";
        return texto.toLowerCase()
                    .replaceAll("[^a-záéíóúñ0-9\\s]", "") // Quita signos de puntuación
                    .trim();
    }

    // TODO (Andrés y Arturo): Implementar aquí el algoritmo de puntuación y desempate
    public Pelicula recomendarPelicula(String mensajeUsuario) {
        List<Pelicula> peliculas = catalogo.getCatálogoPeliculas();
        
        // Lógica pendiente de desarrollo por Andrés y Arturo...
        
        return null;
    }

    public void limpiarMemoriaSesion() {
        this.memoriaActivaSesion.clear();
    }
}