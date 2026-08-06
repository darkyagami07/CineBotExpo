package reportes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstadisticasServicio {

    // Códigos ANSI para formato de consola (rama desarrollo-interfaz)
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String PURPLE = "\u001B[35m";

    public static void mostrarReporteGeneral() {
        String rutaArchivo = "usuarios_preferencias.csv";
        File archivo = new File(rutaArchivo);

        // Validación de existencia de base de datos
        if (!archivo.exists()) {
            System.out.println(YELLOW + "==========================================================" + RESET);
            System.out.println(YELLOW + "     REPORTES ESTADISTICOS - CINEBOT 2026                 " + RESET);
            System.out.println(YELLOW + "==========================================================" + RESET);
            System.out.println(YELLOW + ">>> ADVERTENCIA: Aun no hay interacciones registradas.    " + RESET);
            System.out.println(YELLOW + "==========================================================\n" + RESET);
            return;
        }

        int totalUsuarios = 0;
        Map<String, Integer> peliculasMasRecomendadas = new HashMap<>();
        Map<String, Integer> conteoPorGeneroPersona = new HashMap<>();
        int anioActual = 2026;
        int sumaEdades = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                // Omitir líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Omitir encabezado del CSV
                if (primeraLinea) { 
                    primeraLinea = false; 
                    continue; 
                }

                // Soporte híbrido para delimitadores CSV (coma ',' o punto y coma ';')
                String delimitador = linea.contains(";") ? ";" : ",";
                String[] datos = linea.split(delimitador);

                if (datos.length >= 4) {
                    try {
                        String generoPersona = datos[1].trim();

                        // Normalización de texto para el género de la persona (Ej: "masculino" -> "Masculino")
                        if (!generoPersona.isEmpty()) {
                            generoPersona = generoPersona.substring(0, 1).toUpperCase() + generoPersona.substring(1).toLowerCase();
                        }

                        int anioNacimiento = Integer.parseInt(datos[2].trim());
                        String peliRec = datos[3].trim();

                        // Omitir registros con datos incompletos
                        if (generoPersona.isEmpty() || peliRec.isEmpty()) {
                            continue;
                        }

                        totalUsuarios++;
                        sumaEdades += (anioActual - anioNacimiento);

                        peliculasMasRecomendadas.put(peliRec, peliculasMasRecomendadas.getOrDefault(peliRec, 0) + 1);
                        conteoPorGeneroPersona.put(generoPersona, conteoPorGeneroPersona.getOrDefault(generoPersona, 0) + 1);

                    } catch (NumberFormatException e) {
                        // Captura de filas corruptas sin interrumpir el reporte
                        System.err.println(YELLOW + "Advertencia: Se omitio una fila corrupta en el CSV." + RESET);
                    }
                }
            }

            if (totalUsuarios == 0) {
                System.out.println(YELLOW + ">>> ADVERTENCIA: No se encontraron registros validos para procesar." + RESET);
                return;
            }

            // Animación/Espera visual de procesamiento
            System.out.print(CYAN + "CineBot esta procesando los conjuntos de datos..." + RESET);
            try {
                Thread.sleep(1200);
                System.out.print("\r                                                        \r");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            double promedioEdad = (double) sumaEdades / totalUsuarios;

            // Ordenamiento descendente de películas por frecuencia de recomendación
            List<Map.Entry<String, Integer>> listaPeliculas = new ArrayList<>(peliculasMasRecomendadas.entrySet());
            listaPeliculas.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            // Imprimir Reporte Consolidado
            System.out.println(PURPLE + "[ DIAGNOSTICO DE SISTEMA - CINEBOT 2026 ]\n" + RESET);
            System.out.printf(GREEN + ">>> Operadores analizados: " + RESET + "%-10d " + GREEN + "Media de edad: " + RESET + "%.1f años\n\n", totalUsuarios, promedioEdad);
            
            System.out.println(CYAN + ">>> ANALISIS DEMOGRAFICO DE USUARIOS:" + RESET);
            for (Map.Entry<String, Integer> entry : conteoPorGeneroPersona.entrySet()) {
                double pct = (entry.getValue() * 100.0) / totalUsuarios;
                System.out.printf("    %-15s %4d registros (%5.1f%%)\n", entry.getKey(), entry.getValue(), pct);
            }

            System.out.println(CYAN + "\n>>> TOP PATRONES DE RECOMENDACION DE ALTA FRECUENCIA:" + RESET);
            int limiteTop = Math.min(5, listaPeliculas.size());
            for (int i = 0; i < limiteTop; i++) {
                Map.Entry<String, Integer> entry = listaPeliculas.get(i);
                System.out.printf("    %d. %-35s %3d veces\n", (i + 1), entry.getKey(), entry.getValue());
            }
            System.out.println();

        } catch (IOException e) {
            System.out.println(YELLOW + ">>> ERROR CRITICO DE LECTURA: " + e.getMessage() + RESET);
        }
    }
}