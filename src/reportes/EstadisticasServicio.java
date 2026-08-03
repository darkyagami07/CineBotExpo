package reportes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EstadisticasServicio {

    public static void mostrarReporteGeneral() {
        String archivo = "usuarios_preferencias.csv";
        int totalUsuarios = 0;
        Map<String, Integer> peliculasMasRecomendadas = new HashMap<>();
        Map<String, Integer> conteoPorGeneroPersona = new HashMap<>();
        int añoActual = 2026;
        int sumaEdades = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                if (primeraLinea) { primeraLinea = false; continue; }
                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    totalUsuarios++;
                    String generoPersona = datos[1].trim();
                    int añoNacimiento = Integer.parseInt(datos[2].trim());
                    String peliRec = datos[3].trim();

                    sumaEdades += (añoActual - añoNacimiento);

                    peliculasMasRecomendadas.put(peliRec, peliculasMasRecomendadas.getOrDefault(peliRec, 0) + 1);
                    conteoPorGeneroPersona.put(generoPersona, conteoPorGeneroPersona.getOrDefault(generoPersona, 0) + 1);
                }
            }

            System.out.println("\n ==============================================");
            System.out.println("      REPORTES ESTADISTICOS - CINEBOT 2026      ");
            System.out.println("==============================================");
            if (totalUsuarios == 0) {
                System.out.println("Aun no hay interacciones registradas.");
                return;
            }

            System.out.println("Total de usuarios atendidos: " + totalUsuarios);
            System.out.println("Promedio de edad: " + (sumaEdades / totalUsuarios) + " años.");
            
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

        } catch (IOException | NumberFormatException e) {
            System.out.println("No se pudo procesar el archivo de estadisticas: " + e.getMessage());
        }
    }
}