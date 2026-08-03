import datos.*;
import models.*;
import reportes.EstadisticasServicio;

public class App {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBA INTEGRAL DE CINEBOT ===");

        // 1. Cargar Catalogo
        GestorCatalogo catalogo = GestorCatalogo.getInstancia();
        System.out.println("Catalogo y diccionario listos para usar.");

        // 2. Registro de prueba para verificar guardado en CSV
        try {
            Usuario u1 = new Usuario("Edward", "Masculino", 2007);
            u1.setPeliculaRecomendada("The Office");
            GestorPersistencia.registrarUsuario(u1);
            System.out.println("Usuario de prueba registrado en CSV.");
        } catch (Exception e) {
            System.err.println("Error en persistencia: " + e.getMessage());
        }

        // 3. Generar reportes estadisticos
        System.out.println("\n--- GENERANDO REPORTES ---");
        EstadisticasServicio.mostrarReporteGeneral();
    }
}