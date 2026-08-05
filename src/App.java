import datos.*;

import reportes.EstadisticasServicio;

public class App {
  public static void main(String[] args) {
    System.out.println("=== INICIANDO PRUEBA INTEGRAL DE CINEBOT ===");

    // 1. Cargar Catalogo (Llamamos a la instancia para que cargue los CSV en memoria)
    GestorCatalogo.getInstancia();
    System.out.println("Catalogo y diccionario listos para usar.\n");

    // 2. Iniciar el chat interactivo real
    Registro sesion = new Registro();
    sesion.iniciar();

    // 3. Generar reportes estadisticos
    // (Esto se ejecutará automáticamente cuando el usuario escriba "salir" en el Registro)
    System.out.println("\n--- GENERANDO REPORTES ---");
    EstadisticasServicio.mostrarReporteGeneral();
}
    
}