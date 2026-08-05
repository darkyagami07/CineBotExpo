package reportes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class EstadisticasServicio {

    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String PURPLE = "\u001B[35m";

    public static void mostrarReporteGeneral() {
        String rutaArchivo = "usuarios_preferencias.csv";
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            System.out.println(YELLOW + ">>> ADVERTENCIA: Base de datos no encontrada. Modulo de reportes inactivo." + RESET);
            return;
        }

        int totalUsuarios = 0;
        Map<String, Integer> peliculas = new HashMap<>();
        Map<String, Integer> generos = new HashMap<>();
        int anioActual = 2026;
        int sumaEdades = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                if (primeraLinea) { 
                    primeraLinea = false; 
                    continue; 
                }

                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    try {
                        String generoP = datos[1].trim();
                        
                        if (!generoP.isEmpty()) {
                            generoP = generoP.substring(0, 1).toUpperCase() + generoP.substring(1).toLowerCase();
                        }

                        int anioNac = Integer.parseInt(datos[2].trim());
                        String peli = datos[3].trim();

                        if (generoP.isEmpty() || peli.isEmpty()) continue;

                        totalUsuarios++;
                        sumaEdades += (anioActual - anioNac);
                        peliculas.put(peli, peliculas.getOrDefault(peli, 0) + 1);
                        generos.put(generoP, generos.getOrDefault(generoP, 0) + 1);

                    } catch (NumberFormatException e) {
                        
                    }
                }
            }

            if (totalUsuarios == 0) {
                System.out.println(YELLOW + ">>> ADVERTENCIA: Registros vacios. Esperando interaccion de operadores." + RESET);
                return;
            }

            double promedioEdad = (double) sumaEdades / totalUsuarios;

            List<Map.Entry<String, Integer>> listaPeliculas = new ArrayList<>(peliculas.entrySet());
            listaPeliculas.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            System.out.print(CYAN + "CineBot esta procesando los conjuntos de datos..." + RESET);
            try {
                Thread.sleep(1500);
                System.out.print("\r                                                    \r");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println(PURPLE + "\n[ DIAGNOSTICO DE SISTEMA - CINEBOT 2026 ]\n" + RESET);
            System.out.printf(GREEN + ">>> Operadores analizados: " + RESET + "%-10d " + GREEN + "Media de ciclo vital: " + RESET + "%.1f anos\n\n", totalUsuarios, promedioEdad);
            
            System.out.println(CYAN + ">>> ANALISIS DEMOGRAFICO DE OPERADORES:" + RESET);
            for (Map.Entry<String, Integer> entry : generos.entrySet()) {
                double pct = (entry.getValue() * 100.0) / totalUsuarios;
                System.out.printf("    %-15s %4d registros (%5.1f%%)\n", entry.getKey(), entry.getValue(), pct);
            }

            System.out.println(CYAN + "\n>>> PATRONES DE RECOMENDACION DE ALTA FRECUENCIA:" + RESET);
            for (int i = 0; i < Math.min(5, listaPeliculas.size()); i++) {
                Map.Entry<String, Integer> entry = listaPeliculas.get(i);
                System.out.printf("    %d. %-35s %3d veces\n", (i + 1), entry.getKey(), entry.getValue());
            }
            System.out.println();

        } catch (IOException e) {
            System.out.println(YELLOW + ">>> ERROR CRITICO DE LECTURA: " + e.getMessage() + RESET);
        }
    }
}