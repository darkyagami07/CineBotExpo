package datos;
import java.util.Scanner;
import logica.MotorRecomendaciones;
import logica.ProcesadorPLN;
import models.Usuario; // Así solucionamos la advertencia amarilla de tu línea 6

public class Registro {

    String nombre;
    String genero;
    int fechaNacimiento;
    String ultimaPeliculaRecomendada = "";

    // Colores para la interfaz IA
    String RESET = "\u001B[0m";
    String CYAN = "\u001B[36m";
    String GREEN = "\u001B[32m";
    String YELLOW = "\u001B[33m";

    public void iniciar() {
        Scanner sc = new Scanner(System.in);

        ProcesadorPLN procesador = new ProcesadorPLN();
        MotorRecomendaciones motor = new MotorRecomendaciones();

        // --- 1. PANTALLA DE ARRANQUE (BOOT SEQUENCE) ---
        System.out.println(CYAN + "  ____ _            ____        _   " + RESET);
        System.out.println(CYAN + " / ___(_)_ __   ___| __ )  ___ | |_ " + RESET);
        System.out.println(CYAN + "| |   | | '_ \\ / _ \\  _ \\ / _ \\| __|" + RESET);
        System.out.println(CYAN + "| |___| | | | |  __/ |_) | (_) | |_ " + RESET);
        System.out.println(CYAN + " \\____|_|_| |_|\\___|____/ \\___/ \\__|" + RESET);
        System.out.println(GREEN + " [ CORE VERSION 2026.0 - ACTIVADO ]" + RESET + "\n");

        try {
            System.out.print("Inicializando módulos de lenguaje natural...");
            Thread.sleep(700);
            System.out.println(GREEN + " [OK]" + RESET);
            
            System.out.print("Cargando redes neuronales de recomendación...");
            Thread.sleep(500);
            System.out.println(GREEN + " [OK]" + RESET);
            
            System.out.print("Estableciendo conexión segura...");
            Thread.sleep(400);
            System.out.println(GREEN + " [OK]\n" + RESET);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

      // --- 2. CAPTURA DE DATOS TIPO TERMINAL ---
        System.out.println(YELLOW + "sys_auth> REQUIERE IDENTIFICACIÓN DE USUARIO" + RESET);
        
        // Validación del Nombre
       while (true) {
            System.out.print(CYAN + "[?] Nombre de operador: " + RESET);
            this.nombre = sc.nextLine().trim();
            
            if (this.nombre.isEmpty()) {
                System.out.println(YELLOW + ">>> ERROR: El nombre no puede estar vacío. Intente de nuevo." + RESET);
            } else if (!this.nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
                // Si contiene algo que NO sean letras o espacios, mostramos este error
                System.out.println(YELLOW + ">>> ERROR: El nombre solo puede contener letras. No se permiten números ni caracteres especiales." + RESET);
            } else {
                break; // Si pasa todas las pruebas, rompemos el ciclo y avanzamos
            }
        }

        // Validación del Género
        while (true) {
            System.out.print(CYAN + "[?] Género del operador (Masculino/Femenino/Otro): " + RESET);
            this.genero = sc.nextLine().trim();
            
            if (this.genero.isEmpty()) {
                System.out.println(YELLOW + ">>> ERROR: El género no puede estar vacío. Intente de nuevo." + RESET);
            } else if (!this.genero.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
                // Misma validación: solo letras
                System.out.println(YELLOW + ">>> ERROR: Entrada inválida. Por favor, escriba su género usando solo letras." + RESET);
            } else {
                break; // Si pasa la prueba, avanzamos
            }
        }
        // Validación del Año (Evita el NumberFormatException)
        while (true) {
            System.out.print(CYAN + "[?] Año de fabricación/nacimiento: " + RESET);
            String entradaAnio = sc.nextLine().trim();
            
            try {
                // Intentamos convertir lo que escribió a un número entero
                this.fechaNacimiento = Integer.parseInt(entradaAnio);
                break; // Si tiene éxito, rompemos el ciclo y avanzamos
            } catch (NumberFormatException e) {
                // Si el usuario escribe letras o lo deja vacío, atrapamos el error aquí
                System.out.println(YELLOW + ">>> ERROR: Formato inválido. Debe usar números enteros (ej. 2005). No use letras ni espacios." + RESET);
            }
        }

        // Creación del usuario limpia gracias a la importación superior
        Usuario usuarioActual = new Usuario(this.nombre, this.genero, this.fechaNacimiento, "Sin definir");

        // --- 3. CONFIRMACIÓN Y ENTRADA AL CHAT ---
        System.out.println("\n" + GREEN + ">>> ACCESO CONCEDIDO. Bienvenido/a, " + this.nombre.toUpperCase() + "." + RESET);
        System.out.println(YELLOW + ">>> CINEBOT ESTÁ ESCUCHANDO. (Escribe 'salir' para cerrar la sesión)\n" + RESET);
        System.out.println(GREEN + "CineBot: ¡Hola! Soy tu asistente de recomendaciones. Dime, ¿cómo te sientes hoy o qué tipo de historia te gustaría ver?" + RESET);

        while (true) {
            System.out.print(CYAN + "Tu: " + RESET);
            String entradaUsuario = sc.nextLine();

            // --- LÓGICA DE SALIDA Y GUARDADO EN CSV ---
            if (entradaUsuario.trim().equalsIgnoreCase("salir")) {
                System.out.println(YELLOW + "CineBot: Ha sido un placer hablar contigo. ¡Hasta luego!" + RESET);
                
                try {
                    // Extraemos los datos directamente del Motor para las estadísticas
                    if (motor.getUltimaRecomendada() != null) {
                        usuarioActual.setGeneroPreferido(motor.getUltimaRecomendada().getGenero());
                        usuarioActual.setPeliculaRecomendada(motor.getUltimaRecomendada().getTitulo());
                    } else {
                        // Por si el usuario entra y sale sin pedir nada
                        usuarioActual.setGeneroPreferido("Ninguno");
                        usuarioActual.setPeliculaRecomendada("Ninguna");
                    }
                    
                    // Guardamos en el CSV
                    datos.GestorPersistencia.registrarUsuario(usuarioActual);
                    System.out.println(GREEN + "[OK] Historial guardado correctamente." + RESET);
                } catch (Exception e) {
                    System.err.println("Error al guardar el historial: " + e.getMessage());
                }
                
                break; // Rompe el ciclo y termina el chat
            }

            // --- PROCESAMIENTO PLN ---
            String textoProcesado = procesador.procesadorTexto(entradaUsuario);
            
            // --- EFECTO IA: SIMULAR TIEMPO DE RESPUESTA ---
            System.out.print(YELLOW + "CineBot está analizando el catálogo..." + RESET);
            try {
                // Pausa de 1.2 segundos para darle dramatismo
                Thread.sleep(1200); 
                // Borra el texto de carga de la consola usando retornos de carro
                System.out.print("\r                                            \r"); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // --- BÚSQUEDA Y RESPUESTA FINAL ---
            String respuestaBot = motor.buscarMejorPelicula(textoProcesado);
            System.out.println(GREEN + "CineBot: " + RESET + respuestaBot + "\n");
        }
        
        sc.close();
    }
}