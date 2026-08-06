import java.time.Year;
import java.util.Scanner;
import datos.*;
import models.*;
import logica.*; // 1. Importante: importamos el paquete de la lógica
import reportes.EstadisticasServicio;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== INICIANDO SISTEMA CINEBOT ===");

        // 1. Cargar Catalogo (Singleton)
        GestorCatalogo catalogo = GestorCatalogo.getInstancia();
        System.out.println("Catalogo y diccionario listos para usar.\n");

        // Instanciar el motor de recomendaciones
        MotorRecomendaciones motorRecomendaciones = new MotorRecomendaciones();

        System.out.println("-----------------------------------------");
        System.out.println("     REGISTRO DE USUARIO                 ");
        System.out.println("-----------------------------------------");

        // Captura del Nombre
        System.out.print("Ingresa tu nombre: ");
        String nombre = scanner.nextLine().trim();
        while (nombre.isEmpty()) {
            System.out.print("El nombre no puede estar vacio. Intentalo de nuevo: ");
            nombre = scanner.nextLine().trim();
        }

        // Captura del Genero
        System.out.print("Ingresa tu genero (Masculino/Femenino/Otro): ");
        String genero = scanner.nextLine().trim();
        while (!genero.equalsIgnoreCase("Masculino") && !genero.equalsIgnoreCase("Femenino") && !genero.equalsIgnoreCase("Otro")) {
            System.out.print("Genero invalido. Ingresa solo Masculino, Femenino u Otro: ");
            genero = scanner.nextLine().trim();
        }
        genero = genero.substring(0, 1).toUpperCase() + genero.substring(1).toLowerCase();

        // Captura del Anio de Nacimiento con validacion try-catch
        int anioNacimiento = 0;
        boolean anioValido = false;
        int anioActual = Year.now().getValue();

        while (!anioValido) {
            System.out.print("Ingresa tu año de nacimiento (ej. 2007): ");
            String entradaAnio = scanner.nextLine().trim();
            try {
                anioNacimiento = Integer.parseInt(entradaAnio);
                if (anioNacimiento >= 1900 && anioNacimiento <= anioActual) {
                    anioValido = true;
                } else {
                    System.err.println("Año fuera de rango. Debe estar entre 1900 y " + anioActual + ".");
                }
            } catch (NumberFormatException e) {
                System.err.println("Entrada invalida. Por favor, ingresa un numero entero.");
            }
        }

        // Crear el objeto Usuario con los datos capturados
        Usuario usuarioActual = new Usuario(nombre, genero, anioNacimiento);

        System.out.println("\n-----------------------------------------");
        System.out.println(" Hola " + usuarioActual.getNombre() + "! ¿Cómo te sientes hoy o qué género quieres ver?");
        System.out.println(" (Escribe 'salir' para finalizar la sesion)");
        System.out.println("-----------------------------------------\n");

        // 2. Bucle interactivo del chat
        String entrada = "";
        while (!entrada.equalsIgnoreCase("salir")) {
            System.out.print("Tu > ");
            entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("salir")) {
                System.out.println("\nCineBot: Gracias por consultar CineBot!");
                break;
            }

            if (entrada.isEmpty()) continue;

            // =================================================================
            // INTEGRACIÓN PLN Y MOTOR DE RECOMENDACIÓN
            // =================================================================
            
            // A. Procesamiento de texto (limpieza y sinónimos)
            String textoLimpio = ProcesadorPLN.procesadorTexto(entrada);

            // B. Buscar la mejor película según la intención detectada
            String respuestaBot = motorRecomendaciones.buscarMejorPelicula(textoLimpio);

            // C. Si hubo una película encontrada, actualizamos el atributo del usuario
            if (motorRecomendaciones.getUltimaRecomendada() != null) {
                usuarioActual.setPeliculaRecomendada(motorRecomendaciones.getUltimaRecomendada().getTitulo());
            }

            // D. Imprimir respuesta en consola
            System.out.println("CineBot: " + respuestaBot + "\n");
        }

        // 3. Persistencia de Usuario en CSV al salir
        try {
            GestorPersistencia.registrarUsuario(usuarioActual);
            System.out.println("\n[OK] Datos del usuario registrados en el historial.");
        } catch (Exception e) {
            System.err.println("Error en persistencia: " + e.getMessage());
        }

        // 4. Generar reportes estadisticos
        System.out.println("\n--- GENERANDO REPORTES ---");
        try {
            EstadisticasServicio.mostrarReporteGeneral();
        } catch (Exception e) {
            System.err.println("Error al generar reportes: " + e.getMessage());
        }

        scanner.close();
        System.out.println("=== FIN DE LA SESION ===");
    }
}