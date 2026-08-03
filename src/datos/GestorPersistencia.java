package datos;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import models.Usuario;
import java.io.File;

public class GestorPersistencia {
    private static final String ARCHIVO_USUARIOS = "usuarios_preferencias.csv";

    public static void registrarUsuario(Usuario usuario) {
        File archivo = new File(ARCHIVO_USUARIOS);
        boolean existe = archivo.exists();

        // El 'true' en FileWriter habilita el modo APPEND (no sobreescribe, agrega al final)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS, true))) {
            if (!existe) {
                bw.write("Nombre,Genero,AñoNacimiento,PeliculaRecomendada");
                bw.newLine();
            }
            String linea = String.format("%s,%s,%d,%s",
                    usuario.getNombre(),
                    usuario.getGenero(),
                    usuario.getAñoNacimiento(),
                    usuario.getPeliculaRecomendada());
            bw.write(linea);
            bw.newLine();
            System.out.println("Usuario guardado en el historial.");
        } catch (IOException e) {
            System.err.println("Error al guardar usuario en el historial: " + e.getMessage());
        }
    }
}