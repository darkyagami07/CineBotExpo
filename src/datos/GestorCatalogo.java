package datos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Pelicula;

public class GestorCatalogo {

    // 1. Instancia unica estatica (Singleton)
    private static GestorCatalogo instancia;

    // Referencias protegidas contra modificacion
    private final List<Pelicula> catalogoPeliculas;
    private final Map<String, String> diccionarioSinonimos;

    // 2. Constructor PRIVADO
    private GestorCatalogo() {
        this.catalogoPeliculas = new ArrayList<>();
        this.diccionarioSinonimos = new HashMap<>();
        cargarPeliculasCSV("peliculas.csv");
        cargarSinonimosCSV("sinonimos.csv");
    }

    // 3. Acceso global sincronizado
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

        // Try-with-resources con bloques catch especificos
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                // Validacion 1: Ignorar lineas vacias o de solo espacios
                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Omitir encabezado
                if (primeraLinea) { 
                    primeraLinea = false; 
                    continue; 
                }

                String[] datos = linea.split(",");

                // Validacion 2: Prevenir ArrayIndexOutOfBoundsException si la linea esta incompleta
                if (datos.length < 4) {
                    System.err.println("Advertencia: Se omitio una fila defectuosa (columnas insuficientes) en peliculas.csv");
                    continue;
                }

                try {
                    int id = Integer.parseInt(datos[0].trim());
                    String titulo = datos[1].trim();
                    String genero = datos[2].trim();

                    String palabrasBrutas = datos[3].replace("\"", "").trim().toLowerCase();
                    String[] palabras = palabrasBrutas.split("[-;]");

                    this.catalogoPeliculas.add(new Pelicula(id, titulo, genero, palabras));

                } catch (NumberFormatException e) {
                    System.err.println("Advertencia: Se omitio una fila con ID invalido en peliculas.csv");
                }
            }
            System.out.println("Catalogo cargado correctamente (" + this.catalogoPeliculas.size() + " peliculas).");

        } catch (FileNotFoundException e) {
            System.err.println("Error [FileNotFoundException]: Archivo no localizado - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error [IOException] al leer peliculas.csv: " + e.getMessage());
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
                // Validacion 1: Ignorar lineas vacias
                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Omitir encabezado
                if (primeraLinea) { 
                    primeraLinea = false; 
                    continue; 
                }

                String[] datos = linea.split(",", 2);

                // Validacion 2: Prevenir ArrayIndexOutOfBoundsException
                if (datos.length < 2) {
                    System.err.println("Advertencia: Se omitio una linea invalida en sinonimos.csv");
                    continue;
                }

                String palabraClaveOficial = datos[0].replace("\"", "").trim().toLowerCase();
                String[] listaSinonimos = datos[1].replace("\"", "").split("[\\|,]");

                for (String sinonimo : listaSinonimos) {
                    String sinonimoLimpio = sinonimo.trim().toLowerCase();
                    if (!sinonimoLimpio.isEmpty()) {
                        this.diccionarioSinonimos.put(sinonimoLimpio, palabraClaveOficial);
                    }
                }
            }
            System.out.println("Diccionario de sinonimos cargado (" + this.diccionarioSinonimos.size() + " entradas).");

        } catch (FileNotFoundException e) {
            System.err.println("Error [FileNotFoundException]: Archivo no localizado - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error [IOException] al leer sinonimos.csv: " + e.getMessage());
        }
    }

    // --- GETTERS ---
    public List<Pelicula> getCatalogoPeliculas() { 
        return this.catalogoPeliculas; 
    }

    public Map<String, String> getDiccionarioSinonimos() { 
        return this.diccionarioSinonimos; 
    }
}