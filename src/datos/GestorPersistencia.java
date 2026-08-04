package datos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import models.Usuario;

public class GestorPersistencia {

    private static final String ARCHIVO_USUARIOS = "usuarios_preferencias.csv";

    /**
     * Escribe los datos del usuario y su recomendacion en el archivo CSV.
     * Utiliza el modo anexo (append) para no sobrescribir sesiones anteriores.
     * 
     * @param usuario Objeto con la informacion del usuario capturada en App.
     */
    public static void registrarUsuario(Usuario usuario) {
        if (usuario == null) return;

        File archivo = new File(ARCHIVO_USUARIOS);
        
        // Comprobamos si el archivo no existe O si esta vacio (0 bytes)
        boolean esNuevoOVacio = !archivo.exists() || archivo.length() == 0;

        // FileWriter con el flag 'true' activa el modo anexo (append)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            
            // Si es un archivo nuevo o vacio, escribimos la cabecera
            if (esNuevoOVacio) {
                bw.write("Nombre,Genero,AnioNacimiento,PeliculaRecomendada");
                bw.newLine();
            }

            // Validar que la pelicula no venga nula
            String peli = (usuario.getPeliculaRecomendada() != null && !usuario.getPeliculaRecomendada().isEmpty()) 
                          ? usuario.getPeliculaRecomendada() 
                          : "Sin recomendacion";

            // Formatear la linea CSV
            String linea = String.format("%s,%s,%d,%s",
                    usuario.getNombre(),
                    usuario.getGenero(),
                    usuario.getAnioNacimiento(),
                    peli);

            bw.write(linea);
            bw.newLine(); // Garantiza el salto para la siguiente sesion
            
            System.out.println("[OK] Usuario guardado en el historial.");

        } catch (IOException e) {
            System.err.println("Error al guardar usuario en el historial: " + e.getMessage());
        }
    }
}