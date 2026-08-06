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

    // 1. Instancia única estática (Singleton)
    private static GestorCatalogo instancia;

    // Colecciones del catálogo y diccionario protegidas contra reasignación
    private final List<Pelicula> catalogoPeliculas;
    private final Map<String, String> diccionarioSinonimos;

    // 2. Constructor PRIVADO
    private GestorCatalogo() {
        this.catalogoPeliculas = new ArrayList<>();
        this.diccionarioSinonimos = new HashMap<>();
        cargarPeliculasCSV("peliculas.csv");
        cargarSinonimosCSV("sinonimos.csv");
    }

    // 3. Acceso global sincronizado (Singleton)
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
                // Ignorar líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Omitir encabezado
                if (primeraLinea) { 
                    primeraLinea = false; 
                    continue; 
                }

                // Separar por punto y coma ignorando las comillas internas
                String[] datos = linea.split(";(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Verificar que existan al menos 6 columnas (id, titulo, genero, anio, palabras, mensajeBot)
                if (datos.length < 6) {
                    System.err.println("Advertencia: Se omitio una fila defectuosa (menos de 6 columnas) en peliculas.csv");
                    continue;
                }

                try {
                    int id = Integer.parseInt(datos[0].trim());
                    String titulo = datos[1].trim();
                    String genero = datos[2].trim();
                    int anio = Integer.parseInt(datos[3].trim());

                    String palabrasBrutas = datos[4].replace("\"", "").trim().toLowerCase();
                    String[] palabras = palabrasBrutas.split("[-;]");

                    String mensajeBot = datos[5].replace("\"", "").trim();

                    // Instancia de Pelicula con los 6 campos completos
                    this.catalogoPeliculas.add(new Pelicula(id, titulo, genero, anio, palabras, mensajeBot));

                } catch (NumberFormatException e) {
                    System.err.println("Advertencia: Se omitio una fila con ID o Año invalido en peliculas.csv");
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
                if (linea.trim().isEmpty()) {
                    continue;
                }

                if (primeraLinea) { 
                    primeraLinea = false; 
                    continue; 
                }

                String[] datos = linea.split(";");

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