package datos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Pelicula;

public class GestorCatalogo {
    private static GestorCatalogo instancia;
    private List<Pelicula> catálogoPeliculas;
    private Map<String, String> diccionarioSinonimos;

    private GestorCatalogo() {
        catálogoPeliculas = new ArrayList<>();
        diccionarioSinonimos = new HashMap<>();
        cargarPeliculasCSV("peliculas.csv");
        cargarSinonimosCSV("sinonimos.csv");
    }

    // Método Singleton: Garantiza una única instancia en toda la app
    public static synchronized GestorCatalogo getInstancia() {
        if (instancia == null) {
            instancia = new GestorCatalogo();
        }
        return instancia;
    }

    // Carga con BufferedReader y protección try-catch
    private void cargarPeliculasCSV(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean primeraLinea = true; // Para omitir encabezado del CSV
            while ((linea = br.readLine()) != null) {
                if (primeraLinea) { primeraLinea = false; continue; }
                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    int id = Integer.parseInt(datos[0].trim());
                    String titulo = datos[1].trim();
                    String genero = datos[2].trim();
                    String[] palabras = datos[3].trim().toLowerCase().split(";");
                    catálogoPeliculas.add(new Pelicula(id, titulo, genero, palabras));
                }
            }
            System.out.println("✅ Catálogo cargado correctamente (" + catálogoPeliculas.size() + " películas).");
        } catch (IOException | NumberFormatException e) {
            System.err.println("⚠️ Error al leer peliculas.csv: " + e.getMessage());
        }
    }

    private void cargarSinonimosCSV(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = br.readLine()) != null) {
                if (primeraLinea) { primeraLinea = false; continue; }
                String[] datos = linea.split(",");
                if (datos.length >= 2) {
                    String sinonimo = datos[0].trim().toLowerCase();
                    String palabraClaveOficial = datos[1].trim().toLowerCase();
                    diccionarioSinonimos.put(sinonimo, palabraClaveOficial);
                }
            }
            System.out.println("✅ Diccionario de sinónimos cargado (" + diccionarioSinonimos.size() + " entradas).");
        } catch (IOException e) {
            System.err.println("⚠️ Error al leer sinonimos.csv: " + e.getMessage());
        }
    }

    public List<Pelicula> getCatálogoPeliculas() { return catálogoPeliculas; }
    public Map<String, String> getDiccionarioSinonimos() { return diccionarioSinonimos; }
}