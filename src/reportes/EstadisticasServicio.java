package reportes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EstadisticasServicio {

    public static void mostrarReporteGeneral() {
        String rutaArchivo = "usuarios_preferencias.csv";
        File archivo = new File(rutaArchivo);

        // Validacion previa si el archivo aun no se ha creado
        if (!archivo.exists()) {
            System.out.println("\n==============================================");
            System.out.println("      REPORTES ESTADISTICOS - CINEBOT 2026    ");
            System.out.println("==============================================");
            System.out.println("Aun no hay interacciones registradas (Archivo no encontrado).");
            System.out.println("==============================================\n");
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
                // Omitir lineas vacias
                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Omitir encabezado
                if (primeraLinea) { 
                    primeraLinea = false; 
                    continue; 
                }

                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    try {
                        String generoPersona = datos[1].trim();
                        int anioNacimiento = Integer.parseInt(datos[2].trim());
                        String peliRec = datos[3].trim();

                        // Omitir registros con datos no validos
                        if (generoPersona.isEmpty() || peliRec.isEmpty()) {
                            continue;
                        }

                        totalUsuarios++;
                        sumaEdades += (anioActual - anioNacimiento);

                        peliculasMasRecomendadas.put(peliRec, peliculasMasRecomendadas.getOrDefault(peliRec, 0) + 1);
                        conteoPorGeneroPersona.put(generoPersona, conteoPorGeneroPersona.getOrDefault(generoPersona, 0) + 1);

                    } catch (NumberFormatException e) {
                        // Ignorar filas con anio de nacimiento corrupto sin tumbar la lectura completa
                        System.err.println("Advertencia: Se omitio una fila corrupta en el CSV.");
                    }
                }
            }

            System.out.println("\n==============================================");
            System.out.println("      REPORTES ESTADISTICOS - CINEBOT 2026    ");
            System.out.println("==============================================");
            
            if (totalUsuarios == 0) {
                System.out.println("Aun no hay interacciones validas registradas.");
                System.out.println("==============================================\n");
                return;
            }

            double promedioEdad = (double) sumaEdades / totalUsuarios;

            System.out.println("Total de usuarios atendidos: " + totalUsuarios);
            System.out.printf("Promedio de edad: %.1f años.\n", promedioEdad);
            
            System.out.println("\n--- Distribucion por Genero de Usuario ---");
            for (Map.Entry<String, Integer> entry : conteoPorGeneroPersona.entrySet()) {
                double pct = (entry.getValue() * 100.0) / totalUsuarios;
                System.out.printf("- %s: %d (%.1f%%)\n", entry.getKey(), entry.getValue(), pct);
            }

            System.out.println("\n--- Peliculas/Series Mas Recomendadas ---");
            for (Map.Entry<String, Integer> entry : peliculasMasRecomendadas.entrySet()) {
                System.out.println("- " + entry.getKey() + ": " + entry.getValue() + " veces");
            }
            System.out.println("==============================================\n");

        } catch (IOException e) {
            System.err.println("Error de lectura al procesar estadisticas: " + e.getMessage());
        }
    }
}