import datos.*;
import models.*; // Asegúrate si es models o modelo según tu estructura
import reportes.EstadisticasServicio;

public class App {
    public static void main(String[] args) {
        
        System.out.println("=== INICIALIZANDO CINEBOT ===");

        // 1. Cargar Catálogo (Singleton)
        GestorCatalogo catalogo = GestorCatalogo.getInstancia();

        // 2. Registro de prueba con datos reales
        try {
            Usuario u1 = new Usuario("Edward", "Masculino", 2007);
            u1.setPeliculaRecomendada("The Office");
            
            GestorPersistencia.registrarUsuario(u1);
        } catch (Exception e) {
            System.err.println("Error no controlado: " + e.getMessage());
        }

        // 3. Mostrar Reportes Estadísticos
        EstadisticasServicio.mostrarReporteGeneral();
    }
}