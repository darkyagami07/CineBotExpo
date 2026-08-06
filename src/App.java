import java.time.Year;
import java.util.Scanner;
import datos.*;
import models.*;
import logica.*;
import reportes.EstadisticasServicio;

public class App {

    // Paleta de colores ANSI para la consola
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String PURPLE = "\u001B[35m";
    public static final String RED = "\u001B[31m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Arte ASCII en Cyan brillante
        System.out.println(CYAN + "  ____ ___ _   _ _____ __  __ ___ _   _ ____    ___    _    ");
        System.out.println(" / ___|_ _| \\ | | ____|  \\/  |_ _| \\ | |  _ \\  |_ _|  / \\   ");
        System.out.println("| |    | ||  \\| |  _| | |\\/| || ||  \\| | | | |  | |  / _ \\  ");
        System.out.println("| |___ | || |\\  | |___| |  | || || |\\  | |_| |  | | / ___ \\ ");
        System.out.println(" \\____|___|_| \\_|_____|_|  |_|___|_| \\_|____/  |___/_/   \\_\\" + RESET);
        System.out.println();

        // --- SECUENCIA DE ARRANQUE TIPO IA ---
        String[] secuenciaArranque = {
            "Iniciando secuencia de arranque del nucleo principal...",
            "Cargando modelos de procesamiento de lenguaje natural...",
            "Sincronizando redes neuronales de recomendacion...",
            "Estableciendo protocolos de seguridad de operador..."
        };

        for (String fase : secuenciaArranque) {
            System.out.print(GREEN + "[+] " + RESET + fase);
            try {
                Thread.sleep(800);
                System.out.print("\r                                                              \r");
            } catch (InterruptedException e) {}
        }
        System.out.println(CYAN + ">>> SISTEMA CINEMIND IA [EN LINEA]\n" + RESET);

        GestorCatalogo.getInstancia();
        MotorRecomendaciones motorRecomendaciones = new MotorRecomendaciones();

        System.out.println(YELLOW + ">>> REQUIERE IDENTIFICACION DE OPERADOR" + RESET);

        System.out.print(GREEN + "[?] Ingresa tu nombre: " + RESET);
        String nombre = scanner.nextLine().trim();
        while (nombre.isEmpty()) {
            System.out.print(RED + "[!] El nombre no puede estar vacio. Intentalo de nuevo: " + RESET);
            nombre = scanner.nextLine().trim();
        }

        System.out.print(GREEN + "[?] Ingresa tu genero (Masculino/Femenino/Otro): " + RESET);
        String genero = scanner.nextLine().trim();
        while (!genero.equalsIgnoreCase("Masculino") && !genero.equalsIgnoreCase("Femenino") && !genero.equalsIgnoreCase("Otro")) {
            System.out.print(RED + "[!] Genero invalido. Ingresa solo Masculino, Femenino u Otro: " + RESET);
            genero = scanner.nextLine().trim();
        }
        genero = genero.substring(0, 1).toUpperCase() + genero.substring(1).toLowerCase();

        int anioNacimiento = 0;
        boolean anioValido = false;
        int anioActual = Year.now().getValue();

        while (!anioValido) {
            System.out.print(GREEN + "[?] Ingresa tu año de nacimiento (ej. 2005): " + RESET);
            String entradaAnio = scanner.nextLine().trim();
            try {
                anioNacimiento = Integer.parseInt(entradaAnio);
                if (anioNacimiento >= 1900 && anioNacimiento <= anioActual) {
                    anioValido = true;
                } else {
                    System.err.println(RED + "[!] Año fuera de rango. Debe estar entre 1900 y " + anioActual + "." + RESET);
                }
            } catch (NumberFormatException e) {
                System.err.println(RED + "[!] Entrada invalida. Por favor, ingresa un numero entero." + RESET);
            }
        }

        Usuario usuarioActual = new Usuario(nombre, genero, anioNacimiento);

        // --- MENSAJE DE INICIO DE CHAT TIPO IA ---
        System.out.println(PURPLE + "\n=======================================================" + RESET);
        System.out.println(CYAN + " SESION INICIADA PARA OPERADOR: " + usuarioActual.getNombre().toUpperCase() + RESET);
        System.out.println(CYAN + " CINEMIND IA ESTA ESCUCHANDO TUS PARAMETROS DE BUSQUEDA" + RESET);
        System.out.println(YELLOW + " (Escribe 'salir' para desconectar el enlace)" + RESET);
        System.out.println(PURPLE + "=======================================================\n" + RESET);

        // --- SALUDO INICIAL DEL BOT ---
        System.out.println(CYAN + "Cinemind IA: " + RESET + "Hola, " + usuarioActual.getNombre() + ". ¿Cómo te encuentras el día de hoy? Cuéntame cómo te sientes o qué género buscas.\n");

        String entrada = "";
        while (!entrada.equalsIgnoreCase("salir")) {
            System.out.print(GREEN + "Tu > " + RESET);
            entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("salir")) {
                System.out.println(CYAN + "\nCinemind IA: Desconectando enlace. Ha sido un placer procesar tus datos." + RESET);
                break;
            }

            if (entrada.isEmpty()) continue;
            
            System.out.print(PURPLE + "Cinemind IA esta analizando parametros..." + RESET);
            try {
                Thread.sleep(1000);
                System.out.print("\r                                            \r");
            } catch (InterruptedException e) {}
            
            String textoLimpio = ProcesadorPLN.procesadorTexto(entrada);
            String respuestaBot = motorRecomendaciones.buscarMejorPelicula(textoLimpio);

            if (motorRecomendaciones.getUltimaRecomendada() != null) {
                usuarioActual.setPeliculaRecomendada(motorRecomendaciones.getUltimaRecomendada().getTitulo());
            }

            System.out.println(CYAN + "Cinemind IA: " + RESET + respuestaBot + "\n");
        }

        try {
            GestorPersistencia.registrarUsuario(usuarioActual);
            System.out.println(GREEN + "\n[OK] Encriptando y guardando datos en el historial estadistico." + RESET);
        } catch (Exception e) {
            System.err.println(RED + "Error en persistencia: " + e.getMessage() + RESET);
        }

        System.out.println(YELLOW + "\n>>> GENERANDO REPORTE DE DIAGNOSTICO" + RESET);
        try {
            EstadisticasServicio.mostrarReporteGeneral();
        } catch (Exception e) {
            System.err.println(RED + "Error al generar reportes: " + e.getMessage() + RESET);
        }

        scanner.close();
        System.out.println(PURPLE + "\n[SISTEMA APAGADO]" + RESET);
    }
}