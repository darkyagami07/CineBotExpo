package datos;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import models.Usuario;

public class GestorPersistencia {
    private static final String ARCHIVO_USUARIOS = "usuarios_preferencias.csv";

    public static void registrarUsuario(Usuario usuario) {
        if (usuario == null) return;

        File archivo = new File(ARCHIVO_USUARIOS);
        // Comprobamos si el archivo no existe O si está vacío (0 bytes)
        boolean esNuevoOVacio = !archivo.exists() || archivo.length() == 0;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS, true))) {
            
            // Si es un archivo nuevo o vacío, escribimos el encabezado y hacemos salto de línea
            if (esNuevoOVacio) {
                bw.write("Nombre,Genero,AñoNacimiento,PeliculaRecomendada");
                bw.newLine();
            }

            // Validar que la película no venga nula para no escribir la palabra "null"
            String peli = (usuario.getPeliculaRecomendada() != null) 
                          ? usuario.getPeliculaRecomendada() 
                          : "Sin recomendacion";

            String linea = String.format("%s,%s,%d,%s",
                    usuario.getNombre(),
                    usuario.getGenero(),
                    usuario.getAnioNacimiento(),
                    peli);

            bw.write(linea);
            bw.newLine(); // Garantiza que la SIGUIENTE ejecución también quede en una fila nueva
            
            System.out.println("[OK] Usuario guardado en el historial.");

        } catch (IOException e) {
            System.err.println("Error al guardar usuario en el historial: " + e.getMessage());
        }
    }
}