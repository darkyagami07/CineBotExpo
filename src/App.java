import datos.*;
import models.*;
import reportes.EstadisticasServicio;

public class App {
    public static void main(String[] args) {
        
        System.out.println("=== INICIALIZANDO CINEBOT ===");

        // 1. Cargar Catálogo (Singleton)
        GestorCatalogo catalogo = GestorCatalogo.getInstancia();

        // 2. Simulador de prueba (descomentar para insertar un usuario de prueba)
        try {
            // Usuario de prueba para verificar el flujo completo
            Usuario u1 = new Usuario("Edward", "Masculino", 2007);
            u1.setPeliculaRecomendada("The Office");
            
            // GestorPersistencia.registrarUsuario(u1); // Descomenta si deseas registrar en cada ejecución
        } catch (IllegalArgumentException e) {
            System.err.println("Error en los datos del usuario: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en la ejecucion del sistema: " + e.getMessage());
        }

        // 3. Mostrar Reportes Estadísticos basados en el CSV acumulado
        EstadisticasServicio.mostrarReporteGeneral();
    }
}