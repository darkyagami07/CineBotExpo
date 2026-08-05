package datos;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Pelicula;

public class GestorCatalogo {
    private static GestorCatalogo instancia;
    private List<Pelicula> catalogoPeliculas;
    private Map<String, String> diccionarioSinonimos;

    private GestorCatalogo() {
        catalogoPeliculas = new ArrayList<>();
        diccionarioSinonimos = new HashMap<>();
        cargarPeliculasCSV("peliculas.csv");
        cargarSinonimosCSV("sinonimos.csv");
    }

    // Patron Singleton
    public static synchronized GestorCatalogo getInstancia() {
        if (instancia == null) {
            instancia = new GestorCatalogo();
        }
        return instancia;
    }

    private void cargarPeliculasCSV(String rutaArchivo) {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            System.err.println("Error: No se encontro el archivo " + rutaArchivo);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                if (primeraLinea) { primeraLinea = false; continue; }

                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                   try {
                int id = Integer.parseInt(datos[0].trim());
                String titulo = datos[1].trim();
                String genero = datos[2].trim();
                
                // Palabras clave en la posicion 4
                String palabrasBrutas = datos[4].replace("\"", "").trim().toLowerCase();
                String[] palabras = palabrasBrutas.split(";");
                
                // NUEVO: Extraemos el mensaje del bot en la posicion 5
                String mensajeBot = datos[5].replace("\"", "").trim();

                // AHORA PASAMOS LOS 5 PARÁMETROS AQUÍ
                catalogoPeliculas.add(new Pelicula(id, titulo, genero, palabras, mensajeBot));
                
            } catch (NumberFormatException e) {
                System.err.println("Advertencia: Se omitió una fila con ID inválido.");
            }
                }
            }
            System.out.println("Catalogo cargado correctamente (" + catalogoPeliculas.size() + " peliculas).");
        } catch (IOException e) {
            System.err.println("Error al leer peliculas.csv: " + e.getMessage());
        }
    }

    private void cargarSinonimosCSV(String rutaArchivo) {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            System.err.println("Error: No se encontro el archivo " + rutaArchivo);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                if (primeraLinea) { primeraLinea = false; continue; }

                // Separar palabra oficial de sinonimos por la primera coma
                String[] datos = linea.split(",", 2);

                if (datos.length >= 2) {
                    String palabraClaveOficial = datos[0].replace("\"", "").trim().toLowerCase();
                   String[] listaSinonimos = datos[1].replace("\"", "").split("\\|");

                    for (String sinonimo : listaSinonimos) {
                        String sinonimoLimpio = sinonimo.trim().toLowerCase();
                        if (!sinonimoLimpio.isEmpty()) {
                            diccionarioSinonimos.put(sinonimoLimpio, palabraClaveOficial);
                        }
                    }
                }
            }
            System.out.println("Diccionario de sinonimos cargado (" + diccionarioSinonimos.size() + " entradas).");
        } catch (IOException e) {
            System.err.println("Error al leer sinonimos.csv: " + e.getMessage());
        }
    }

    // --- GETTERS ---
    public List<Pelicula> getCatalogoPeliculas() { 
        return catalogoPeliculas; 
    }

    public Map<String, String> getDiccionarioSinonimos() { 
        return diccionarioSinonimos; 
    }
}